package com.example.aws.helper;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aws.config.AWSConfig;

@Component
public class AWSHelper {
	
	@Autowired
	AWSConfig config;
	
	private final String NEW_LINE = "\n";
	
	/*
	 * Example of how to sign an AWS GET request using Signature Version 4 signing process
	 * First, set values for accesskey and secretkey in application.yml
	 * then this method will return the secured URL using query parameters
	 * 
	 * @Param String endpoint - non-secured AWS URL (this code assumes no other query params already exist in
	 * the endpoint string)
	 */
	public String getSecureUrl(String endpoint) throws Exception {
		final String method = "GET";
		
		URL url = new URL(endpoint);
		String host = url.getHost();
		String canonicalUri = url.getPath();
		
		OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
		
		DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
		String amzDate = utc.format(utcFormatter);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
		String dateStamp = utc.format(formatter);
		
		String credentialScope = dateStamp + "/" + config.getRegion() + "/" + config.getService() + "/" + "aws4_request";
		
		String canonicalHeaders = "host:" + host + NEW_LINE;
		String signedHeaders = "host";
		
		String canonicalQueryString = "X-Amz-Algorithm=" + config.getAlgorithm();
		canonicalQueryString+= "&X-Amz-Credential=" + URLEncoder.encode(config.getAccesskey() + "/" + credentialScope, StandardCharsets.UTF_8.toString());
		canonicalQueryString+= "&X-Amz-Date=" + amzDate;
		canonicalQueryString+= "&X-Amz-Expires=8640";
		canonicalQueryString+= "&X-Amz-SignedHeaders=" + signedHeaders;
		
		String payloadHash = "UNSIGNED-PAYLOAD";
		
		String canonicalRequest = method + 
				NEW_LINE + 
				canonicalUri + 
				NEW_LINE + 
				canonicalQueryString + 
				NEW_LINE + 
				canonicalHeaders + 
				NEW_LINE + 
				signedHeaders + 
				NEW_LINE + 
				payloadHash;
		
		String stringToSign = config.getAlgorithm() + 
				NEW_LINE + 
				amzDate + 
				NEW_LINE + 
				credentialScope +
				NEW_LINE + 
				DigestUtils.sha256Hex(canonicalRequest);

		byte[] signingKey = getSignatureKey(config.getSecretkey(), dateStamp, config.getRegion(), config.getService());
		
		String signature = Hex.encodeHexString(HmacSHA256(stringToSign, signingKey));
		
		canonicalQueryString+= "&X-Amz-Signature=" + signature;
		
		String secureUrl = endpoint + "?" + canonicalQueryString;
		
		return secureUrl;
	}
	
	private byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
	    byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
	    byte[] kDate =    HmacSHA256(dateStamp, kSecret);
	    byte[] kRegion =  HmacSHA256(regionName, kDate);
	    byte[] kService = HmacSHA256(serviceName, kRegion);
	    byte[] kSigning = HmacSHA256("aws4_request", kService); 
	   
	    return kSigning;
	}
	
	private byte[] HmacSHA256(String data, byte[] key) throws Exception {
	    String algorithm="HmacSHA256";
	    Mac mac = Mac.getInstance(algorithm);
	    mac.init(new SecretKeySpec(key, algorithm));
	    return mac.doFinal(data.getBytes("UTF8"));
	}

}
