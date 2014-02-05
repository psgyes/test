import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;


public class OAuthBasicExample {
	
	/*0단계 URL 및 Consumer 정보 설정*/
//	  -> Request URLs
//	  - Request Token URL
//	  - User Authorization URL
//	  - Access Token URL

	static final String REQUEST_TOKEN_URL = "https://apis.daum.net/oauth/requestToken";
	static final String AUTHORIZE_URL = "https://apis.daum.net/oauth/authorize";
	static final String ACCESS_TOKEN_URL = "https://apis.daum.net/oauth/accessToken";
	 
	static final String CONSUMER_KEY = "f851c4a4-826e-45da-968e-ae4516eedb91";
	static final String CONSUMER_SECRET = "aoqF.X5_72JI5hn_GPIloOS3F4R8rzQj1MOKn_FaT4gKRIhFezTssw00";
	 
	static final String API_URL = "https://apis.daum.net";
	 
	// Service Provider 객체 생성
	static OAuthProvider provider = new DefaultOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZE_URL);  

	// Consumer 객체 생성
	static OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
	//소스느 git 피피티는 jira 첨부&메일로 보내
	
	
	public static void main(String[] args) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException, IOException, URISyntaxException  {		
		/*1단계 request token 요청 */
		// User가 인증을하려고 하면 그 요청을 인식한 Consumer는 Service Provider에게 토큰을 요청된다면(Consumer -> Service Provider)
		// Service Provider가 토큰을 생성해서 돌려준다.(수행)(Consumer <- Service Provider)
		String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
		
		/*2단계 login 페이지로 리다이렉트*/
		// 토큰을 받은 Consumer가 User를 Service Provider의 인증페이지로 토큰과 함께 리다이렉트 (수행)(Consumer -> User)
		System.out.println("아래 URL로 가서 사용자 인증을 하시면 인증코드(verifier)를 얻을 수 있습니다.");
		System.out.println(authUrl);
		
		System.out.print("인증코드 입력 : ");
		// 여기까지 하면 인증이 되고 Service Provider가 User를 Consumer로 리다이렉트(User -> Consumer)
		Scanner s = new Scanner(System.in);		
		String verifier = s.next();
		/*3단계 access token 요청*/
		// Consumer가 access token을 Service Provider에게 요청한다면(Consumer -> Service Provider)
		// Service Provider는 Consumer에게 access token을 발급(Consumer <- Service Provider)
		provider.retrieveAccessToken(consumer, verifier);

		// 여기서는 변수에 저장하지만, 실제로 개발을 할 때는 Access Token이 발급된 이후,
		// FileSystem, DB 등 저장공간에 넣어두고, 이후에는 발급절차를 생략할 수 있다. 
			
		final String ACCESS_TOKEN = consumer.getToken();
		final String TOKEN_SECRET = consumer.getTokenSecret();
		
		// 미리 저장된 Access token 정보는 setTokenWithSecret() 메소드로 지정할 수 있다.
		consumer.setTokenWithSecret(ACCESS_TOKEN, TOKEN_SECRET);
	
		URL url = new URL(API_URL + "/cafe/favorite_cafes.json");		
		HttpURLConnection request = (HttpURLConnection) url.openConnection();

		// oauth_signature 값을 얻습니다.
		// OAuth 스팩상 보호된 자원에 접근할 때는 oauth_signature 값을 생성해야 함
		consumer.sign(request);		

		request.connect();

		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String tmpStr = "";
		while( (tmpStr = br.readLine()) != null) {
			System.out.println(tmpStr);
		}

	}

}
