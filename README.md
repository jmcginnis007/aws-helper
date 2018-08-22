# aws-helper

Java example of how to sign an AWS GET request using Signature Version 4 signing process.

Simply fill in your access key and secret key in the application.yml file then run the "AwsHelperApplicationTests" unit test using your IDE or maven:

 mvn -Dtest=AwsHelperApplicationTests#testSecureUrl test
 
 Output will be something like:
 
http://aws.example.com/test.jpg?
X-Amz-Algorithm=AWS4-HMAC-SHA256&
X-Amz-Credential=%3CACCESS_KEY%3E%2F20180822%2Fus-west-1%2Fs3%2Faws4_request&
X-Amz-Date=20180822T055339Z&
X-Amz-Expires=8640&
X-Amz-SignedHeaders=host&
X-Amz-Signature=64efc8ee406ae545412266c1389b123f3c32b95c5e6a9b787e86034081e67c48
