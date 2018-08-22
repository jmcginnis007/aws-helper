package com.example.aws;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.aws.helper.AWSHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AwsHelperApplicationTests {

	@Test
	public void contextLoads() {
	}
	
	@Autowired
	AWSHelper awsHelper;
	
	@Test
	public void testSecureUrl() throws Exception {
		String secureUrl = awsHelper.getSecureUrl("http://aws.example.com/test.jpg");
		
		System.out.println(secureUrl);
		
		assertTrue("missing header X-Amz-Algorithm", secureUrl.contains("X-Amz-Algorithm"));
		assertTrue("missing header X-Amz-Credential", secureUrl.contains("X-Amz-Credential"));
		assertTrue("missing header X-Amz-Date", secureUrl.contains("X-Amz-Date"));
		assertTrue("missing header X-Amz-Expires", secureUrl.contains("X-Amz-Expires"));
		assertTrue("missing header X-Amz-SignedHeaders", secureUrl.contains("X-Amz-SignedHeaders"));
		assertTrue("missing header X-Amz-Signature", secureUrl.contains("X-Amz-Signature"));
	}

}
