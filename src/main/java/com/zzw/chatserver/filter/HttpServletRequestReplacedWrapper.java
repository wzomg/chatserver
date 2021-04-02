package com.zzw.chatserver.filter;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpServletRequestReplacedWrapper extends
        HttpServletRequestWrapper {
    private final byte[] body; // 报文

    public HttpServletRequestReplacedWrapper(HttpServletRequest request)
            throws IOException {
        super(request);
        body = readBytes(request.getReader());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

    private byte[] readBytes(BufferedReader br) throws IOException {
        String str = null;
        StringBuilder retStr = new StringBuilder();
        while ((str = br.readLine()) != null) {
            retStr.append(str);
        }
        if (StringUtils.isNotBlank(retStr.toString())) {
            return retStr.toString().getBytes(StandardCharsets.UTF_8);
        }
        return null;
    }
}
