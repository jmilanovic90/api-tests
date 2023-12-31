package api.tests.rest.client;

import api.tests.storage.Storage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.DecoderConfig.decoderConfig;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.config.RedirectConfig.redirectConfig;

/**
 * @author Jovana Milanovic (j.milanovic@stresstest.rs)
 * @since 26.09.23.
 */
@Slf4j
@Scope("cucumber-glue")
public class BaseRestClient {
    private Storage storage;
    // HTTP Timeouts in milliseconds
    private static final int HTTP_CONNECTION_TIMEOUT = 180000; // the time to establish the connection with the remote host
    private static final int HTTP_SOCKET_TIMEOUT = 600000; // the time waiting for data – after the connection was established; maximum time of inactivity between two data packets
    private static final int HTTP_CONNECTION_MANAGER_TIMEOUT = 10000; // the time to wait for a connection from the connection manager/pool

    @Getter(AccessLevel.PROTECTED)
    final ObjectMapperConfig objectMapperConfig = new ObjectMapperConfig().jackson2ObjectMapperFactory((aClass, s) -> {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.registerModule(new SimpleModule().addSerializer(BigInteger.class, new ToStringSerializer()));
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd[ HH:mm:ss]"));
        return objectMapper;
    });
    @Getter(AccessLevel.PROTECTED)
    private final SessionFilter sessionFilter = new SessionFilter();
    @Getter(AccessLevel.PROTECTED)
    private final CookieFilter cookieFilter = new CookieFilter();
    @Getter(AccessLevel.PROTECTED)
    private final ErrorLoggingFilter errorLoggingFilter = new ErrorLoggingFilter(getLogPrintStream());
    @Getter(AccessLevel.PROTECTED)
    private final RequestLoggingFilter requestLoggingFilter = new RequestLoggingFilter(LogDetail.ALL, getLogPrintStream());
    @Getter(AccessLevel.PROTECTED)
    private final ResponseLoggingFilter responseLoggingFilter = new ResponseLoggingFilter(LogDetail.ALL, getLogPrintStream());
    @Getter(AccessLevel.PROTECTED)
    private final RequestSpecBuilder defaultRequestSpecBuilder = new RequestSpecBuilder();

    public BaseRestClient(final String baseUrl, Storage storage) {
        setBaseUri(baseUrl);
        configSetup();
        setCommonHeaders();
        this.storage = storage;
    }

    private void setBaseUri(final String baseUri) {
        getDefaultRequestSpecBuilder().setBaseUri(baseUri);
    }

    private void configSetup() {
        getDefaultRequestSpecBuilder().setConfig(
                config().logConfig(logConfig().enablePrettyPrinting(true))
                        .encoderConfig(encoderConfig().defaultContentCharset("UTF-8"))
                        .decoderConfig(decoderConfig().defaultContentCharset("UTF-8"))
                        .objectMapperConfig(getObjectMapperConfig())
                        .redirect(redirectConfig().followRedirects(false))
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", HTTP_CONNECTION_TIMEOUT)
                                .setParam("http.socket.timeout", HTTP_SOCKET_TIMEOUT)
                                .setParam("http.connection-manager.timeout", HTTP_CONNECTION_MANAGER_TIMEOUT)));
    }

    private void setCommonHeaders() {
        getDefaultRequestSpecBuilder()
                .setContentType(ContentType.JSON);
        getDefaultRequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setAccept(ContentType.TEXT)
                .setAccept(ContentType.ANY);
    }

    public Response post(final Object body, final Map<String, List<String>> parameters, final String path) {
        final RequestSpecBuilder requestSpecificationBuilder = new RequestSpecBuilder()
                .addRequestSpecification(getDefaultRequestSpecBuilder().build());
        setBody(body, requestSpecificationBuilder);
        setParameters(parameters, requestSpecificationBuilder);
        // @formatter:off
		return given()
			.log()
			.body()
			.spec(requestSpecificationBuilder.build())
			.filters(getSessionFilter(), getCookieFilter(), getRequestLoggingFilter(), getResponseLoggingFilter(), getErrorLoggingFilter())
			.when()
			.post(path)
			.then()
			.extract()
			.response();
		// @formatter:on
    }

    public Response postWithHeaders(final Map<String, String> headers, final Object body, final String path) {
        final RequestSpecBuilder requestSpecificationBuilder = new RequestSpecBuilder()
                .addRequestSpecification(getDefaultRequestSpecBuilder().build());
        setBody(body, requestSpecificationBuilder);

        addHeaders(headers, requestSpecificationBuilder);
        // @formatter:off
        return given()
                .relaxedHTTPSValidation()
                .log()
                .body()
                .spec(requestSpecificationBuilder.build())
                .filters(getSessionFilter(), getCookieFilter(), getRequestLoggingFilter(), getResponseLoggingFilter(), getErrorLoggingFilter())
                .when()
                .post(path)
                .then()
                .extract()
                .response();
        // @formatter:on
    }

    public Response get(final Map<String, ?> parameters, final String path) {
        final RequestSpecBuilder requestSpecificationBuilder = new RequestSpecBuilder()
                .addRequestSpecification(getDefaultRequestSpecBuilder().build());
        setParameters(parameters, requestSpecificationBuilder);
        // @formatter:off
		return given()
                .log()
			.uri()
			.spec(requestSpecificationBuilder.build())
			.filters(getSessionFilter(), getCookieFilter(), getRequestLoggingFilter(), getResponseLoggingFilter(), getErrorLoggingFilter())
			.when()
			.get(path)
			.then()
			.extract()
			.response();
		// @formatter:on
    }

    /**
     * Execute HTTP DELETE method.
     *
     * @param parameters request query parameters
     * @param path       endpoint path
     * @return response as {@link Response}
     */
    public Response delete(final Map<String, String> parameters, final String path) {
        final RequestSpecBuilder requestSpecificationBuilder = new RequestSpecBuilder()
                .addRequestSpecification(getDefaultRequestSpecBuilder().build());
        setParameters(parameters, requestSpecificationBuilder);
        // @formatter:off
        return given()
                .spec(requestSpecificationBuilder.build())
                .filters(getSessionFilter(), getCookieFilter(), getRequestLoggingFilter(), getResponseLoggingFilter(), getErrorLoggingFilter())
                .when()
                .delete(path)
                .then()
                .extract()
                .response();
        // @formatter:on
    }

    private void setParameters(final Map<String, ?> parameters, final RequestSpecBuilder requestSpecificationBuilder) {
        if (null != parameters) {
            requestSpecificationBuilder.addQueryParams(parameters);
        }
    }

    protected void setBody(final Object body, final RequestSpecBuilder requestSpecificationBuilder) {
        if (null != body) {
            requestSpecificationBuilder.setBody(body);
        }
    }

    /**
     * Add Headers to Request Specification.
     *
     * @param headers                     request headers
     * @param requestSpecificationBuilder request specification builder
     */
    protected void addHeaders(final Map<String, String> headers, final RequestSpecBuilder requestSpecificationBuilder) {
        if (null != headers) {
            requestSpecificationBuilder.addHeaders(headers);
        }
    }

    private PrintStream getLogPrintStream() {
        final OutputStream output = new OutputStream() {
            StringBuilder myStringBuilder = new StringBuilder();

            @Override
            public void write(final int b) {
                this.myStringBuilder.append((char) b);
            }

            @Override
            public void flush() {
                log.debug(myStringBuilder.toString());
                myStringBuilder = new StringBuilder();
            }
        };
        return new PrintStream(output, true);
    }
}
