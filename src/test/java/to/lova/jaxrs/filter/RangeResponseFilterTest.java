package to.lova.jaxrs.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * Test for {@link RangeResponseFilter}.
 */
public class RangeResponseFilterTest {

    private ContainerRequestContext requestContext;

    private ContainerResponseContext responseContext;

    private MultivaluedMap<String, String> requestHeaders;

    private MultivaluedMap<String, String> responseHeaders;

    @BeforeEach
    private void init() {
        this.requestContext = Mockito.mock(ContainerRequestContext.class);
        this.responseContext = Mockito.mock(ContainerResponseContext.class);
        this.requestHeaders = new MultivaluedHashMap<>();
        this.responseHeaders = new MultivaluedHashMap<>();
        Mockito.doReturn(this.requestHeaders).when(this.requestContext).getHeaders();
        Mockito.doReturn(this.responseHeaders).when(this.responseContext).getHeaders();
        MediaType mediaType = Mockito.mock(MediaType.class);
        Mockito.doReturn(mediaType).when(this.responseContext).getMediaType();
        Mockito.doReturn("text/plain").when(mediaType).toString();
    }

    /**
     * Tests the filter with If-Range (E-Tag) header.
     */
    @Test
    public void filterIfRangeEtagTest() {
        this.requestHeaders.putSingle("Range", "bytes=6-10");
        this.requestHeaders.putSingle("If-Range", "qwerty");
        Mockito.doReturn("bytes=6-10").when(this.requestContext).getHeaderString("Range");
        Mockito.doReturn("qwerty").when(this.requestContext).getHeaderString("If-Range");

        this.responseHeaders.putSingle(HttpHeaders.ETAG, "qwerty");
        Mockito.doReturn("qwerty").when(this.responseContext).getHeaderString(HttpHeaders.ETAG);
        Mockito.doReturn(new ByteArrayOutputStream()).when(this.responseContext).getEntityStream();

        RangeResponseFilter filter = new RangeResponseFilter();
        try {
            filter.filter(this.requestContext, this.responseContext);
        } catch (IOException e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests the filter with If-Range (Last-Modified) header.
     */
    @Test
    public void filterIfRangeLastModifiedTest() {
        this.requestHeaders.putSingle("Range", "bytes=6-10");
        this.requestHeaders.putSingle("If-Range", "Wed, 21 Oct 2015 07:28:00 GMT");
        Mockito.doReturn(this.requestHeaders).when(this.requestContext).getHeaders();
        Mockito.doReturn("bytes=6-10").when(this.requestContext).getHeaderString("Range");
        Mockito.doReturn("Wed, 21 Oct 2015 07:28:00 GMT").when(this.requestContext).getHeaderString("If-Range");

        this.responseHeaders.putSingle(HttpHeaders.LAST_MODIFIED, "Wed, 21 Oct 2015 07:28:00 GMT");
        Mockito.doReturn("Wed, 21 Oct 2015 07:28:00 GMT").when(this.responseContext)
                .getHeaderString(HttpHeaders.LAST_MODIFIED);
        Mockito.doReturn(new ByteArrayOutputStream()).when(this.responseContext).getEntityStream();

        RangeResponseFilter filter = new RangeResponseFilter();
        try {
            filter.filter(this.requestContext, this.responseContext);
        } catch (IOException e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests the filter without If-Range header.
     */
    @Test
    public void filterWithoutIfRangeTest() {
        this.requestHeaders.putSingle("Range", "bytes=6-10");
        Mockito.doReturn(this.requestHeaders).when(this.requestContext).getHeaders();
        Mockito.doReturn("bytes=6-10").when(this.requestContext).getHeaderString("Range");

        Mockito.doReturn(this.responseHeaders).when(this.responseContext).getHeaders();
        Mockito.doReturn(new ByteArrayOutputStream()).when(this.responseContext).getEntityStream();

        RangeResponseFilter filter = new RangeResponseFilter();
        try {
            filter.filter(this.requestContext, this.responseContext);
        } catch (IOException e) {
            Assertions.fail(e);
        }
    }

}
