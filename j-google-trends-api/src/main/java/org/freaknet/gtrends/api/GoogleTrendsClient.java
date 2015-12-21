/**
 * Copyright (C) 2013 Marco Tizzoni <marco.tizzoni@gmail.com>
 *
 * This file is part of j-google-trends-api
 *
 *     j-google-trends-api is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     j-google-trends-api is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with j-google-trends-api.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freaknet.gtrends.api;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsClientException;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a client for Google Trends https://www.google.com/trends/ .
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleTrendsClient {

  private static final Logger logger = LoggerFactory.getLogger(GoogleTrendsClient.class);
  private final GoogleAuthenticator authenticator;
  private final HttpClient client;

  /**
   *
   * @param authenticator
   * @param client
   */
  public GoogleTrendsClient(GoogleAuthenticator authenticator, HttpClient client) {
    this.authenticator = authenticator;
    this.client = client;
  }

  /**
   * Execute the request through the given proxy.
   *
   * @param request
   * @param proxy
   * @return content The content of the response
   * @throws GoogleTrendsClientException
   */
  public String execute(GoogleTrendsRequest request, HttpHost proxy) throws GoogleTrendsClientException {
    String html = null;
    try {
      HttpRequestBase builtRequest = request.build(proxy);
	  html = run(request, builtRequest);
    } catch (ClientProtocolException ex) {
      throw new GoogleTrendsClientException(ex);
    } catch (IOException ex) {
      throw new GoogleTrendsClientException(ex);
    } catch (ConfigurationException ex) {
      throw new GoogleTrendsClientException(ex);
    } catch (GoogleTrendsRequestException ex) {
      throw new GoogleTrendsClientException(ex);
    }

    return html;
  }
  
  /**
   * Execute the request.
   *
   * @param request
   * @return content The content of the response
   * @throws GoogleTrendsClientException
   */
  public String execute(GoogleTrendsRequest request) throws GoogleTrendsClientException {
    String html = null;
    try {
      HttpRequestBase builtRequest = request.build();
	  html = run(request, builtRequest);
    } catch (ClientProtocolException ex) {
      throw new GoogleTrendsClientException(ex);
    } catch (IOException ex) {
      throw new GoogleTrendsClientException(ex);
    } catch (ConfigurationException ex) {
      throw new GoogleTrendsClientException(ex);
    } catch (GoogleTrendsRequestException ex) {
      throw new GoogleTrendsClientException(ex);
    }

    return html;
  }

  private String run(GoogleTrendsRequest request, HttpRequestBase builtRequest)
	throws IOException, ClientProtocolException, ConfigurationException, GoogleTrendsClientException {
	String html;
	logger.debug("Query: {}", builtRequest.toString());

      HttpResponse response = client.execute(builtRequest);
      logger.debug("Status: {}", response.getStatusLine());
      Header[] httpHeader = response.getHeaders("Location");
      while (httpHeader.length > 0) {
    	String location = httpHeader[0].getValue();
        logger.debug("Query: {}", location);
		HttpGet httpGet = new HttpGet(location);
        response = client.execute(httpGet);
        httpHeader = response.getHeaders("Location");
      }
      html = GoogleUtils.toString(response.getEntity().getContent());
      logger.debug("Result: {}", html);

      Pattern p = Pattern.compile(GoogleConfigurator.getConfiguration().getString("google.trends.client.reError"), Pattern.CASE_INSENSITIVE);
      Matcher matcher = p.matcher(html);
      if (matcher.matches()) {
        throw new GoogleTrendsClientException("*** You are running too fast man! Looks like you reached your quota limit. Wait a while and slow it down with the '-S' option! *** ");
      }
	return html;
  }
}
