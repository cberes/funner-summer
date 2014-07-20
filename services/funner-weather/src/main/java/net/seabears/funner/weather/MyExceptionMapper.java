package net.seabears.funner.weather;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.grizzly.utils.Exceptions;

@Provider
public class MyExceptionMapper implements
    ExceptionMapper<WebApplicationException>
{
  @Override
  public Response toResponse(WebApplicationException ex)
  {
    return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode())
        .entity(Exceptions.getStackTraceAsString(ex))
        .type(MediaType.TEXT_PLAIN).build();
  }
}