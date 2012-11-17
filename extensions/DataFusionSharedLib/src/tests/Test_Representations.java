package tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import sun.datafusion.data.*;

// Tests all the MySQL table representations
public class Test_Representations {

	private static int RANDOM_ITERATIONS = 10000;

	@Test
	public void testDataFusion() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {

			// Create fields
			int id = Test_Random.randInt();
			int nodeID = Test_Random.randInt();
			int dataSource_ID = Test_Random.randInt();
			int dataStored_ID = Test_Random.randInt();
			String title = Test_Random.randString();
			String url = Test_Random.randString();
			String summary = Test_Random.randString();
			boolean approved = Test_Random.randBool();
			int rating = Test_Random.randInt();
			Date timestamp = Test_Random.randDate();

			// Create object
			DataFusion test = new DataFusion(id, nodeID, dataSource_ID,
					dataStored_ID, title, url, summary, approved, rating,
					timestamp);

			// Check values
			assertTrue(test != null);
			assertTrue(test.getId() == id);
			assertTrue(test.getNodeID() == nodeID);
			assertTrue(test.getDataSource_id() == dataSource_ID);
			assertTrue(test.getDataStored_id() == dataStored_ID);
			assertTrue(test.getTitle().compareTo(title) == 0);
			assertTrue(test.getUrl().compareTo(url) == 0);
			assertTrue(test.getSummary().compareTo(summary) == 0);
			assertTrue(test.isApproved() == approved);
			assertTrue(test.getRating() == rating);
			assertTrue(test.getTimestamp().compareTo(timestamp) == 0);

			// Modify fields
			id = Test_Random.randInt();
			nodeID = Test_Random.randInt();
			dataSource_ID = Test_Random.randInt();
			dataStored_ID = Test_Random.randInt();
			title = Test_Random.randString();
			url = Test_Random.randString();
			summary = Test_Random.randString();
			approved = Test_Random.randBool();
			rating = Test_Random.randInt();
			timestamp = Test_Random.randDate();

			// Make changes
			test.setId(id);
			test.setNodeID(nodeID);
			test.setDataSource_id(dataSource_ID);
			test.setDataStored_id(dataStored_ID);
			test.setTitle(title);
			test.setUrl(url);
			test.setSummary(summary);
			test.setApproved(approved);
			test.setRating(rating);
			test.setTimestamp(timestamp);

			// Check values
			assertTrue(test != null);
			assertTrue(test.getId() == id);
			assertTrue(test.getNodeID() == nodeID);
			assertTrue(test.getDataSource_id() == dataSource_ID);
			assertTrue(test.getDataStored_id() == dataStored_ID);
			assertTrue(test.getTitle().compareTo(title) == 0);
			assertTrue(test.getUrl().compareTo(url) == 0);
			assertTrue(test.getSummary().compareTo(summary) == 0);
			assertTrue(test.isApproved() == approved);
			assertTrue(test.getRating() == rating);
			assertTrue(test.getTimestamp().compareTo(timestamp) == 0);
		}
	}

	@Test
	public void testDataMeans() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {

			// Create fields
			int id = Test_Random.randInt();
			int DataSource_id = Test_Random.randInt();
			String name = Test_Random.randString();
			String url = Test_Random.randString();
			int type = Test_Random.randInt();
			Date lastProcessed = Test_Random.randDate();

			// Create object
			DataMeans test = new DataMeans(id, DataSource_id, name, url, type,
					lastProcessed);

			// Test fields
			assertTrue(test.getId() == id);
			assertTrue(test.getDataSource_id() == DataSource_id);
			assertTrue(test.getName().compareTo(name) == 0);
			assertTrue(test.getUrl().compareTo(url) == 0);
			assertTrue(test.getType() == type);
			assertTrue(test.getLastProcessed() == lastProcessed);

			// Modify fields
			id = Test_Random.randInt();
			DataSource_id = Test_Random.randInt();
			name = Test_Random.randString();
			url = Test_Random.randString();
			type = Test_Random.randInt();
			lastProcessed = Test_Random.randDate();

			// Set fields
			test.setId(id);
			test.setDataSource_id(DataSource_id);
			test.setName(name);
			test.setUrl(url);
			test.setType(type);
			test.setLastProcessed(lastProcessed);

			// Test fields
			assertTrue(test.getId() == id);
			assertTrue(test.getDataSource_id() == DataSource_id);
			assertTrue(test.getName().compareTo(name) == 0);
			assertTrue(test.getUrl().compareTo(url) == 0);
			assertTrue(test.getType() == type);
			assertTrue(test.getLastProcessed() == lastProcessed);
		}
	}

	@Test
	public void testDataSource() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {

			// Create fields
			int id = Test_Random.randInt();
			String url = Test_Random.randString();
			String name = Test_Random.randString();
			String logourl = Test_Random.randString();

			// Create object
			DataSource test = new DataSource(id, url, name, logourl);

			// Test fields
			assertTrue(test.getId() == id);
			assertTrue(test.getUrl().compareTo(url) == 0);
			assertTrue(test.getName().compareTo(name) == 0);
			assertTrue(test.getLogourl().compareTo(logourl) == 0);

			// Modify fields
			id = Test_Random.randInt();
			url = Test_Random.randString();
			name = Test_Random.randString();
			logourl = Test_Random.randString();

			// Set fields
			test.setId(id);
			test.setUrl(url);
			test.setName(name);
			test.setLogourl(logourl);

			// Test fields
			assertTrue(test.getId() == id);
			assertTrue(test.getUrl().compareTo(url) == 0);
			assertTrue(test.getName().compareTo(name) == 0);
			assertTrue(test.getLogourl().compareTo(logourl) == 0);
		}
	}

	@Test
	public void testDataStored() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {

			// Create fields
			int id = Test_Random.randInt();
			int DataMeans_id = Test_Random.randInt();
			String title = Test_Random.randString();
			String url = Test_Random.randString();
			String data = Test_Random.randString();
			String linkUrl = Test_Random.randString();
			String linkedData = Test_Random.randString();
			Date timestamp = Test_Random.randDate();
			boolean indexed = Test_Random.randBool();

			// Create object
			DataStored test = new DataStored(id, DataMeans_id, title, url,
					data, linkUrl, linkedData, timestamp, indexed);

			// Test fields
			assertTrue(test.getId() == id);
			assertTrue(test.getDataMeans_id() == DataMeans_id);
			assertTrue(test.getTitle().compareTo(title) == 0);
			assertTrue(test.getUrl().compareTo(url) == 0);
			assertTrue(test.getData().compareTo(data) == 0);
			assertTrue(test.getLinkedUrl().compareTo(linkUrl) == 0);
			assertTrue(test.getLinkedData().compareTo(linkedData) == 0);
			assertTrue(test.getTimestamp().compareTo(timestamp) == 0);
			assertTrue(test.isIndexed() == indexed);

			// Modify fields
			id = Test_Random.randInt();
			DataMeans_id = Test_Random.randInt();
			title = Test_Random.randString();
			url = Test_Random.randString();
			data = Test_Random.randString();
			linkUrl = Test_Random.randString();
			linkedData = Test_Random.randString();
			timestamp = Test_Random.randDate();
			indexed = Test_Random.randBool();

			// Set fields
			test.setId(id);
			test.setDataMeans_id(DataMeans_id);
			test.setTitle(title);
			test.setUrl(url);
			test.setData(data);
			test.setLinkedUrl(linkUrl);
			test.setLinkedData(linkedData);
			test.setTimestamp(timestamp);
			test.setIndexed(indexed);

			// Test fields
			assertTrue(test.getId() == id);
			assertTrue(test.getDataMeans_id() == DataMeans_id);
			assertTrue(test.getTitle().compareTo(title) == 0);
			assertTrue(test.getUrl().compareTo(url) == 0);
			assertTrue(test.getData().compareTo(data) == 0);
			assertTrue(test.getLinkedUrl().compareTo(linkUrl) == 0);
			assertTrue(test.getLinkedData().compareTo(linkedData) == 0);
			assertTrue(test.getTimestamp().compareTo(timestamp) == 0);
			assertTrue(test.isIndexed() == indexed);
		}
	}

	@Test
	public void testNode() {
		for (int i = 0; i < RANDOM_ITERATIONS; i++) {
			// Create fields
			int nodeID = Test_Random.randInt();
			List<String> tags = new LinkedList<String>();
			int randStrings = Test_Random.randInt() % 100;
			for (int j = 0; j < randStrings; j++) {
				tags.add(Test_Random.randString());
			}
			boolean foundTags[] = new boolean[tags.size()];

			// Create object
			Node test = new Node(nodeID);
			for (int j = 0; j < tags.size(); j++)
				test.addTag(tags.get(j));

			// Test fields
			assertTrue(test.getNodeID() == nodeID);
			assertTrue(test.getTags().size() == tags.size());
			for (int j = 0; j < tags.size(); j++)
				foundTags[j] = false;
			for (int j = 0; j < test.getTags().size(); j++) {
				for (int k = 0; k < tags.size(); k++) {
					if (!foundTags[k]
							&& test.getTags().get(j).compareTo(tags.get(k)) == 0) {
						foundTags[k] = true;
						break;
					}
				}
			}
			for (int j = 0; j < tags.size(); j++)
				assertTrue(foundTags[j]);

			// Modify fields
			randStrings = Test_Random.randInt() % 100;
			for (int j = 0; j < randStrings; j++) {
				String randString = Test_Random.randString();
				tags.add(randString);
				test.addTag(randString);
			}
			foundTags = new boolean[tags.size()];

			// Test fields
			assertTrue(test.getNodeID() == nodeID);
			assertTrue(test.getTags().size() == tags.size());
			for (int j = 0; j < tags.size(); j++)
				foundTags[j] = false;
			for (int j = 0; j < test.getTags().size(); j++) {
				for (int k = 0; k < tags.size(); k++) {
					if (!foundTags[k]
							&& test.getTags().get(j).compareTo(tags.get(k)) == 0) {
						foundTags[k] = true;
						break;
					}
				}
			}
			for (int j = 0; j < tags.size(); j++)
				assertTrue(foundTags[j]);
		}
	}

}
