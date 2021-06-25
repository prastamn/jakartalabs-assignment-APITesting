package com.jakartalabs.assignment3may2021;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.gson.internal.LinkedTreeMap;
import com.jakartalabs.assignment3may2021.utils.DataUtils;
import com.jakartalabs.assignment3may2021.utils.TestUtils;

import io.restassured.response.Response;

public class APITests extends BaseAPITest {

	String authToken = "token not found";
	String emailRegister;

	@Test(priority = 1)
	public void testRegisterAPI() {

		LinkedTreeMap<String, Object> registerMap = TestUtils.convertJsonToMap(DataUtils
				.getDataFromExcel("Payload", "RegisterAPI").replace("uniqueFirstName", faker.name().firstName())
				.replace("uniqueLastName", faker.name().lastName())
				.replace("uniqueEmail", faker.name().username() + "@gmail.com")
				.replace("uniquePhone", "+62-81231231231"));

		Response loginResponse = given().spec(commonSpec).body(registerMap).when().post(APIEndpoints.registerAPI);

		verifyAPIStatusTimeAndHeader(loginResponse, 200);

		authToken = getDataFromResponseUsingJsonPath(loginResponse, JsonPaths.authToken);
		emailRegister = getDataFromResponseUsingJsonPath(loginResponse, JsonPaths.email);

		assertEquals(authToken.contains("token not found"), false);

	}

	@Test(priority = 2)
	public void verifyProfileAPI() {

		Response profileResponse = given().spec(commonSpec).header("authtoken", authToken).when()
				.get(APIEndpoints.profileAPI);

		verifyAPIStatusTimeAndHeader(profileResponse, 200);

		String emailProfile = getDataFromResponseUsingJsonPath(profileResponse, JsonPaths.emailProfile);
		assertEquals(emailProfile, emailRegister);

	}

	@Test(priority = 3)
	public void logoutAPI() {

		Response logoutResponse = given().spec(commonSpec).param("authtoken", authToken).when()
				.delete(APIEndpoints.logoutAPI);

		verifyAPIStatusTimeAndHeader(logoutResponse, 200);
	}
}
