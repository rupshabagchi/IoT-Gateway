/*
 * Copyright Ericsson AB 2011-2014. All Rights Reserved.
 *
 * The contents of this file are subject to the Lesser GNU Public License,
 *  (the "License"), either version 2.1 of the License, or
 * (at your option) any later version.; you may not use this file except in
 * compliance with the License. You should have received a copy of the
 * License along with this software. If not, it can be
 * retrieved online at https://www.gnu.org/licenses/lgpl.html. Moreover
 * it could also be requested from Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * BECAUSE THE LIBRARY IS LICENSED FREE OF CHARGE, THERE IS NO
 * WARRANTY FOR THE LIBRARY, TO THE EXTENT PERMITTED BY APPLICABLE LAW.
 * EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR
 * OTHER PARTIES PROVIDE THE LIBRARY "AS IS" WITHOUT WARRANTY OF ANY KIND,

 * EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE
 * LIBRARY IS WITH YOU. SHOULD THE LIBRARY PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING
 * WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR
 * REDISTRIBUTE THE LIBRARY AS PERMITTED ABOVE, BE LIABLE TO YOU FOR
 * DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL
 * DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE LIBRARY
 * (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED
 * INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE
 * OF THE LIBRARY TO OPERATE WITH ANY OTHER SOFTWARE), EVEN IF SUCH
 * HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 */
package com.ericsson.deviceaccess.coap.basedriver.util;

import com.ericsson.common.util.BitUtil;
import com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPContentType;
import com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionHeader;
import com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.ACCEPT;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.BLOCK1;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.BLOCK2;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.CONTENT_FORMAT;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.ETAG;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.IF_MATCH;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.IF_NONE_MATCH;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.LOCATION_PATH;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.LOCATION_QUERY;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.MAX_AGE;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.OBSERVE;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.PROXY_URI;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.URI_HOST;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.URI_PATH;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.URI_PORT;
import static com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPOptionName.URI_QUERY;
import com.ericsson.deviceaccess.coap.basedriver.api.message.CoAPUtil;

/**
 * This is a helper class to convert option header to string format from byte
 * arrays
 *
 */
public class CoAPOptionHeaderConverter {

    /**
     * Constructor. Creates an instance of the converter.
     */
    public CoAPOptionHeaderConverter() {

    }

    /**
     * Convert the value of the option header to string. There are different
     * formats for option headers, so different headers are handled differently
     * based on the format. For now, Etag, Token and If-Match are converted to
     * hex strings.
     *
     * @param header header whose value is to be converted
     * @return string representation of the header value
     */
    public String convertOptionHeaderToString(CoAPOptionHeader header) {
        switch (header.getOptionName()) {
            case CONTENT_FORMAT:
            case URI_PORT:
            case ACCEPT:
            case OBSERVE:
                return convertShortToString(header);
            case MAX_AGE:
//            case MAX_OFE:
                return convertIntToString(header);
            case PROXY_URI:
            case URI_HOST:
            case URI_QUERY:
            case URI_PATH:
            case LOCATION_QUERY:
            case LOCATION_PATH:
                return new String(header.getValue());
            case ETAG:
            case IF_MATCH: // TODO etag, token and if-match
                // TODO check if the byte array is hex or string??
                return this.convertHexToString(header);
            case BLOCK1:
            case BLOCK2:
                // show the value as unsigned int
                long longValue = this.convertIntToUnsignedLong(header);
                // More details for debug (Ryoji)
                //value = Long.toString(longValue);
                long num = longValue >> 4;
                long m = (longValue >> 3) & 0x1L;
                long szx = longValue & 7L;
                return longValue + " (Num=" + num + "/M=" + m + "/Sz=" + CoAPUtil.getBlockSize(szx) + ")";
            case IF_NONE_MATCH:
            default:
                return "";
        }
    }

    public boolean isHex(String in) {
        boolean isHex = false;
        try {
            // try to parse the string to an integer, using 16 as radix
            Integer.parseInt(in, 16);
            isHex = true;
        } catch (NumberFormatException e) {
        }
        return isHex;
    }

    /**
     * Convert value of the option header byte array to a hex string
     *
     * @param header header to be converted
     * @return hex string representation of a byte array
     */
    private String convertHexToString(CoAPOptionHeader header) {
        byte[] valueBytes = header.getValue();

        char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        StringBuilder buff = new StringBuilder(valueBytes.length * 2);

        for (int i = 0; i < valueBytes.length; i++) {
            buff.insert(i * 2, chars[(valueBytes[i] >> 4) & 0xf]);
            buff.insert(i * 2 + 1, chars[valueBytes[i] & 0xf]);
        }

        return buff.toString();
    }

    /**
     * Convert short (0-2 bytes) to string. First the short will be converted to
     * unsigned int.
     *
     * @param header header value to be converted
     * @return string representation of the short
     */
    private String convertShortToString(CoAPOptionHeader header) {
        // content type, uri-port and accept headers' length 0-2 bytes
        String value = "";
        int unsignedShort = this.shortToUnsignedInt(header);
        if (header.getOptionName() == CONTENT_FORMAT) {
            value = CoAPContentType.getContentTypeName(unsignedShort)
                    .getContentType();
        } else if (header.getOptionName() == OBSERVE
                || header.getOptionName() == ACCEPT
                || header.getOptionName() == URI_PORT) {
            value = Integer.toString(unsignedShort);
        }
        return value;
    }

    /**
     * Convert short to unsigned int. Headers Content-Type, Uri-Port, Accept,
     * and Observe are max two byte uints.
     *
     * @param h
     * @return unsigned int representation of the short
     */
    public int shortToUnsignedInt(CoAPOptionHeader h) {
        int unsignedShort = -1;

        if (h.getOptionName() == CONTENT_FORMAT
                || h.getOptionName() == URI_PORT
                || h.getOptionName() == ACCEPT
                || h.getOptionName() == OBSERVE) {

            byte[] headerBytes = h.getValue();
            short shortInt = 0;
            if (headerBytes.length == 1) {
                shortInt = BitUtil.mergeBytesToShort((byte) 0, headerBytes[0]);
            } else if (headerBytes.length == 2) {
                shortInt = BitUtil.mergeBytesToShort(headerBytes[0], headerBytes[1]);
            }
            unsignedShort = shortInt & 0xFFFF;
        }
        return unsignedShort;
    }

    /**
     * Convert integer (max 4 bytes) to string. Max-Age and Max-Ofe headers are
     * maximum 4 byte uints.
     *
     * @param header
     * @return string representation of the integer value
     */
    private String convertIntToString(CoAPOptionHeader header) {
        String value = "";
        long unsignedLong = this.convertIntToUnsignedLong(header);
        value = Long.toString(unsignedLong);
        return value;
    }

    /**
     *
     * Convert integer (max 4 bytes) to unsigned long. Max-Age and Max-Ofe
     * headers are maximum 4 byte uints.
     *
     * @param h
     * @return
     */
    public long convertIntToUnsignedLong(CoAPOptionHeader h) {
        long unsignedLong = -1;
        if (h.getOptionNumber() == MAX_AGE.getNo()
                || h.getOptionNumber() == CoAPOptionName.BLOCK1.getNo()
                || h.getOptionNumber() == CoAPOptionName.BLOCK2.getNo()) {
            byte[] valueBytes = h.getValue();

            int intValue = 0;
            if (valueBytes.length == 1) {
                byte[] emptyByte = new byte[1];
                intValue = BitUtil.mergeBytesToInt(emptyByte[0],
                        emptyByte[0], emptyByte[0], valueBytes[0]);

            } else if (valueBytes.length == 2) {
                byte[] emptyByte = new byte[1];
                intValue = BitUtil.mergeBytesToInt(emptyByte[0],
                        emptyByte[0], valueBytes[0], valueBytes[1]);

            } else if (valueBytes.length == 3) {
                byte[] emptyByte = new byte[1];
                intValue = BitUtil.mergeBytesToInt(emptyByte[0],
                        valueBytes[0], valueBytes[1], valueBytes[2]);

            } else if (valueBytes.length == 4) {
                intValue = BitUtil.mergeBytesToInt(valueBytes[0],
                        valueBytes[1], valueBytes[2], valueBytes[3]);

            }
            unsignedLong = 0xffffffffL & intValue;
        }

        return unsignedLong;
    }
}
