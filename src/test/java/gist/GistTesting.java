package gist;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasItems;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.IssueService;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;

public class GistTesting implements IGitHubConstants {

	@BeforeTest
	public void BeforeTest() {

		RestAssured.baseURI = HOST_API;
		RestAssured.oauth2(AUTH_TOKEN);

	}

	/*
	 * CUROperationsGistTest: This method We will create , update and read Gits
	 */

	@Test
	public void CUROperationsGistTest() {

		// Create A New Gist:

		// Create a file with description
		GistFile file = new GistFile().setContent(DEFAULT_CONTENT);

		// Create a Gist and add description, type, File
		Gist gist = new Gist();
		gist.setDescription(GITS_TEXT2);
		gist.setFiles(Collections.singletonMap(FILE_NAME, file));
		gist.setPublic(true);

		// Create request URL using string builder
		StringBuilder uri = new StringBuilder(SEGMENT_GISTS);

		// Send POST request
		Gist responseGist = given().auth().oauth2(AUTH_TOKEN).body(gist).when().post(uri.toString()).as(Gist.class);

		// Verify Gist Creation in Using Description in request text
		Assert.assertEquals(responseGist.getDescription(), GITS_TEXT2);

		// Edit a gist

		String gitsId = responseGist.getId();

		responseGist.setDescription(GITS_TEXT);
		// Create request URL
		uri.setLength(0);
		uri.append(SEGMENT_GISTS).append('/').append(gitsId);

		// create patch request
		Gist responseGist2 = given().auth().oauth2(AUTH_TOKEN).body(responseGist).when().patch(uri.toString())
				.as(Gist.class);

		// Verify Gist description updation
		Assert.assertEquals(responseGist2.getDescription(), GITS_TEXT);
		Assert.assertEquals(responseGist2.getId(), gitsId);

		// Read a gists
		Gist responseGist3 = given().auth().oauth2(AUTH_TOKEN).body(responseGist2).when().get(uri.toString())
				.as(Gist.class);

		// Verify Gist read operation
		Assert.assertEquals(responseGist3.getDescription(), GITS_TEXT);
		Assert.assertEquals(responseGist3.getId(), gitsId);
	}

	/*
	 * DeleteGistTest: This method is used delete a gist created by create gist test
	 * This method is also customize to delete any gist based on discription or id
	 */

	@Test(dependsOnMethods = { "CUROperationsGistTest", "CommentOprationsGistTest" })
	public void DeleteGistTest() {

		// Get Gist id using gist description

		StringBuilder uri = new StringBuilder(SEGMENT_USERS);
		uri.append(SEGMENT_USERNAME).append(SEGMENT_GISTS);

		List<String> ids = given().auth().oauth2(AUTH_TOKEN).when().get(uri.toString()).then().assertThat()
				.body(SEGMENT_DESCRIPTION, hasItems(GITS_TEXT)).extract().path(SEGMENT_ID);

		// Get first git from the array
		String gistId = ids.get(0).toString();

		// Create request URL
		uri.setLength(0);
		uri.append(SEGMENT_GISTS).append('/').append(gistId);

		// Delete Gits based on ID and verify status code
		given().auth().oauth2(AUTH_TOKEN).when().delete(uri.toString()).then().assertThat().statusCode(204);

	}

	/*
	 * ListsGistCommitsTest: This method is used list commits a gist
	 */

	@Test(dependsOnMethods = { "CUROperationsGistTest", "CommentOprationsGistTest" })
	public void ListsGistCommitsTest() {

		// Get Gist id using gist description

		// Create request URL using string builder
		StringBuilder uri = new StringBuilder(SEGMENT_USERS);
		uri.append(SEGMENT_USERNAME).append(SEGMENT_GISTS);

		List<String> ids = given().auth().oauth2(AUTH_TOKEN).when().get(uri.toString()).then().assertThat()
				.body(SEGMENT_DESCRIPTION, hasItems(GITS_TEXT)).extract().path(SEGMENT_ID);

		// Get first git from the array
		String gistId = ids.get(0).toString();

		// Create request URL
		uri.setLength(0);
		uri.append(SEGMENT_GISTS).append('/').append(gistId).append(SEGMENT_COMMITS);

		// List Gits based on ID and verify status code
		given().auth().oauth2(AUTH_TOKEN).when().get(uri.toString()).then().assertThat().statusCode(200);

	}

	@Test(enabled = false)
	public void ForkGistTest() {

		// Get Gist id using gist description

		StringBuilder uri = new StringBuilder(SEGMENT_USERS);
		uri.append(SEGMENT_USERNAME).append(SEGMENT_GISTS);

		List<String> ids = given().auth().oauth2(AUTH_TOKEN).when().get(uri.toString()).then().assertThat()
				.body(SEGMENT_DESCRIPTION, hasItems(GITS_TEXT)).extract().path(SEGMENT_ID);

		// Get first git from the array
		String gistId = ids.get(0).toString();

		// Create request URL
		uri.setLength(0);
		uri.append(SEGMENT_GISTS).append('/').append(gistId).append(SEGMENT_FORKS);

		System.out.println(uri.toString());

		given().auth().oauth2(AUTH_TOKEN).when().post(uri.toString()).then().assertThat().statusCode(201);

	}

	/*
	 * StarOperationsGistTest: This method We will get any gists for a user and than
	 * we will first check it status and than we will star and unstar gists and
	 * check status
	 */

	@Test(dependsOnMethods = { "CUROperationsGistTest", "CommentOprationsGistTest" })
	public void StarOperationsGistTest() throws InterruptedException {

		// Get Gist id using gist description

		StringBuilder uri = new StringBuilder(SEGMENT_USERS);
		uri.append(SEGMENT_USERNAME).append(SEGMENT_GISTS);

		List<String> ids = given().auth().oauth2(AUTH_TOKEN).when().get(uri.toString()).then().assertThat()
				.body(SEGMENT_DESCRIPTION, anything()).extract().path(SEGMENT_ID);

		// Get first git from the array
		String gistId = ids.get(0).toString();

		// Create request URL
		uri.setLength(0);
		uri.append(SEGMENT_GISTS).append('/').append(gistId).append(SEGMENT_STAR);

		// get status of star for a gists

		given().auth().oauth2(AUTH_TOKEN).when().get(uri.toString()).then().assertThat().statusCode(404);

		// star a gists
		given().auth().oauth2(AUTH_TOKEN).when().put(uri.toString()).then().assertThat().statusCode(204);

		// get status of star for a gists

		given().auth().oauth2(AUTH_TOKEN).when().get(uri.toString()).then().assertThat().statusCode(204);

		// unstar a gists
		given().auth().oauth2(AUTH_TOKEN).when().delete(uri.toString()).then().assertThat().statusCode(204);
	}

	/*
	 * CommentOprationsGistTest: This method We will get any gists for a user and
	 * than we will perform CURD operation for comments
	 */

	@Test(dependsOnMethods = { "CUROperationsGistTest" })
	public void CommentOprationsGistTest() throws InterruptedException {

		// Get Gist id using gist description

		StringBuilder uri = new StringBuilder(SEGMENT_USERS);
		uri.append(SEGMENT_USERNAME).append(SEGMENT_GISTS);

		List<String> ids = given().auth().oauth2(AUTH_TOKEN).when().get(uri.toString()).then().assertThat()
				.body(SEGMENT_DESCRIPTION, anything()).extract().path(SEGMENT_ID);

		// Get first git from the array
		String gistId = ids.get(0).toString();

		// Create request URL
		uri.setLength(0);

		uri.append(SEGMENT_GISTS).append('/').append(gistId).append(SEGMENT_COMMENTS);

		// get status of comment for a gists

		given().auth().oauth2(AUTH_TOKEN).when().get(uri.toString()).then().assertThat().statusCode(200).extract()
				.path(SEGMENT_ID);

		// Create a map of comments
		Map<String, String> params = Collections.singletonMap(IssueService.FIELD_BODY, GITS_COMMENT);

		// Create a comment request and check status
		List<Integer> commentIds = given().auth().oauth2(AUTH_TOKEN).body(params).when().post(uri.toString()).then()
				.assertThat().statusCode(201).extract().path(SEGMENT_ID);

		// Update a map of comments
		Map<String, String> params2 = Collections.singletonMap(IssueService.FIELD_BODY, GITS_COMMENT2);

		uri.append('/').append(commentIds.get(0));

		System.out.println(uri.toString());
		// Create a update request and check status
		given().auth().oauth2(AUTH_TOKEN).body(params2).when().patch(uri.toString()).then().assertThat()
				.statusCode(200);

		// Create a delete request
		given().auth().oauth2(AUTH_TOKEN).when().delete(uri.toString()).then().assertThat().statusCode(204);

	}
}
