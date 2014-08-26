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
package com.ericsson.research.connectedhome.common;

import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.HttpHeaders;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class BasicAuthFilterTest {

    private JUnit4Mockery context = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private ClientRequest clientRequest;
    private MultivaluedMapImpl headers;
    private BasicAuthFilter authFilter;
    private ClientHandler dummy;

    @Before
    public void setup() {
        dummy = context.mock(ClientHandler.class);
        authFilter = new BasicAuthFilter("admin", "lamepass");
        ReflectionTestUtils.setField(authFilter, "next", dummy);
        clientRequest = context.mock(ClientRequest.class);

        headers = new MultivaluedMapImpl();

        context.checking(new Expectations() {
            {
                allowing(clientRequest).getHeaders();
                will(returnValue(headers));
                allowing(dummy).handle(with(aNonNull(ClientRequest.class)));
                will(returnValue(null));
            }
        });
    }

    @Test
    public void test() {
        authFilter.handle(clientRequest);

        assertTrue("" + headers.get(HttpHeaders.AUTHORIZATION), headers.get(HttpHeaders.AUTHORIZATION).contains(
                "Basic YWRtaW46bGFtZXBhc3M="));
    }

    @Test
    public void testAuthAlreadySet() {
        headers.add(HttpHeaders.AUTHORIZATION, "banan");

        authFilter.handle(clientRequest);

        assertTrue(!headers.get(HttpHeaders.AUTHORIZATION).contains(
                "Basic YWRtaW46bGFtZXBhc3M="));
    }

    @Test
    public void testEqualsAndHashCode() {
        BasicAuthFilter authFilter2 = new BasicAuthFilter("admin", "lamepass");
        assertEquals(authFilter, authFilter2);
        assertEquals(authFilter.hashCode(), authFilter2.hashCode());
    }
}
