/**
 * 
 */
package devtests;

import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;
import java.net.URLEncoder;
import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class FirstFetchTest {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private final String
		BASE_URL	= "http://localhost:8080/tm/",
		USER_ID		= ITQCoreOntology.SYSTEM_USER,
		VERB		= "GetTopic";

	/**
	 * 
	 */
	public FirstFetchTest() {
		qb = new QueryBuilder();
		client = new SimpleHttpClient();
		JSONObject query = qb.coreQuery(VERB, USER_ID, null, null);
		query.put(ITQCoreOntology.LOCATOR_PROPERTY, "ClassType");
		String q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("A "+q);
		IResult r = client.get(BASE_URL, query.toJSONString());
		System.out.println("B "+r.getErrorString()+" | "+r.getResultObject());
		
	}
//before modifying to getFullNode
//B  | {"rMsg":"ok","rToken":"","cargo":{"crDt":"1523587622142","trCl":["TypeType"],"isLiv":true,"crtr":"SystemUser","node_type":null,"isVrt":false,"lox":"ClassType","isPrv":false,"_ver":"1523242824583","lEdDt":"1523587622142","label":{"en":["Class type"]},"url":null}}

}
/**
 * after modifying topic map to getFullNode
{
	"rMsg": "ok",
	"rToken": "",
	"cargo": {
		"crtr": "SystemUser",
		"_ver": "1523242824583",
		"lEdDt": "1523587622142",
		"label": {
			"en": ["Class type"]
		},
		"url": null,
		"crDt": ["1523587622142", "2018-04-08T20:00:24-07:00"],
		"trCl": ["TypeType"],
		"tpL": ["ClassTypeSubclassRelationTypeTypeType", "NodeTypeSubclassRelationTypeClassType", "HarvestedClassTypeSubclassRelationTypeClassType", "ClusterTypeSubclassRelationTypeClassType", "fb33c569-63b6-405a-9ede-d84b5f5516a0InstanceRelationTypeClassType", "590e4c32-b957-4240-8ce0-00ac1ecade05InstanceRelationTypeClassType", "2964b63a-1400-4b2b-89a0-4572a1a30115InstanceRelationTypeClassType", "f0a72cfa-10a5-45b8-a54d-582d3da0bd8dInstanceRelationTypeClassType", "a3aff3c0-5252-4f6a-a3d0-08b2a1a0c787InstanceRelationTypeClassType", "5bd837e9-007a-42dc-bb60-8fee3437d74aInstanceRelationTypeClassType", "68a4beab-dd6d-4ad8-b83a-4c3e06a5eb11InstanceRelationTypeClassType", "8188e0dd-f491-4430-8231-a55493ff3fcfInstanceRelationTypeClassType", "b28170b0-b379-4cdb-b63d-60a3aa5b9cc7InstanceRelationTypeClassType", "9bd81384-4690-4b41-8062-b18dfc8ba7d1InstanceRelationTypeClassType", "c893f41d-8395-477f-abe7-08cf86911bebInstanceRelationTypeClassType", "cc4ce6ec-0c7e-4b3b-863a-53260cb82aa1InstanceRelationTypeClassType", "cd27bdb9-29b1-475c-a6e0-aecabb5bf8e0InstanceRelationTypeClassType", "f3e5d341-1fcf-4f62-b062-0d57698c23d4InstanceRelationTypeClassType", "cbe511d9-4c9d-430a-8063-3f31b5bacda1InstanceRelationTypeClassType", "527df67a-056c-47ff-b3e9-266afd6e30fdInstanceRelationTypeClassType", "a71fe617-de5d-4a54-8eb0-9009f8e154d8InstanceRelationTypeClassType", "3f4d9ec7-5e07-4bfb-bec6-821c8a8df047InstanceRelationTypeClassType", "21273f53-775c-410a-8fa3-841429d6abecInstanceRelationTypeClassType", "da8446fb-5b88-4f5e-84f4-ac220213c72dInstanceRelationTypeClassType", "3a63bb76-09cc-4ce7-80ae-d8cdd580fcacInstanceRelationTypeClassType", "6d73a1e2-033e-4a66-b276-b33fee5790f5InstanceRelationTypeClassType", "4333ddcc-36cd-4bdc-b97b-e3ef9434434cInstanceRelationTypeClassType", "c0043767-b99f-427d-b5c0-4ab99c1aac57InstanceRelationTypeClassType", "b0b29c38-762d-4aa6-bc56-5ffef86d62d6InstanceRelationTypeClassType", "0916f9d1-27b6-4662-af30-d75e9e169ccaInstanceRelationTypeClassType", "075aea9a-152f-4ee1-9c3c-80f81e8625abInstanceRelationTypeClassType", "dbc2acff-5200-48de-bd96-b4167a7db360InstanceRelationTypeClassType", "b0fc8c5e-a526-4fb6-a399-97c508acb620InstanceRelationTypeClassType", "d1634990-e3af-449a-8b3f-0cfff87e7993InstanceRelationTypeClassType", "62db5ea8-c445-4893-a97f-26253ac2c15aInstanceRelationTypeClassType", "e6be3fc6-81a9-43b7-8fd3-1df91b19e17eInstanceRelationTypeClassType", "856ae934-c447-4815-b02f-5313fd8ccb09InstanceRelationTypeClassType", "c68b66b8-6baf-4e25-bf28-1643e864d296InstanceRelationTypeClassType", "0847ffc8-19d4-4e69-89b4-7647ef563142InstanceRelationTypeClassType", "2c128620-ae5b-4185-aacd-e46b37eb6ba8InstanceRelationTypeClassType", "fa4a6769-68cb-4dfc-8679-2ce47122b50cInstanceRelationTypeClassType", "704bd983-c032-4293-ba5f-f8b3c3718694InstanceRelationTypeClassType", "027a4730-cdfb-491a-8aa9-1f5e361b566fInstanceRelationTypeClassType", "5ae5cdfe-2ee9-4c4f-aa2a-6bde14a8d54cInstanceRelationTypeClassType", "a91550a5-7a37-4b45-8212-c963b043a007InstanceRelationTypeClassType", "fe0e36ab-2a09-4395-a450-acdc7a06d5f8InstanceRelationTypeClassType", "a928406e-2159-4711-b0c6-fdeee4827c52InstanceRelationTypeClassType", "6a47c282-9212-47af-b494-8b91f3143233InstanceRelationTypeClassType", "498d7876-274b-4d50-a449-87c2dd3831baInstanceRelationTypeClassType", "8760ec43-4434-456f-b982-bdc0df0fd2c9InstanceRelationTypeClassType", "d2aae66b-b52d-4212-9593-eaf1613cc89fInstanceRelationTypeClassType", "172720db-7735-470e-a3ba-2bbc68cc101cInstanceRelationTypeClassType", "8b4a6eaf-6fe9-4229-bd57-8b6107316146InstanceRelationTypeClassType", "41b5bf4b-7321-4a59-8007-bca3c454c35fInstanceRelationTypeClassType", "ec1bffd0-1e59-4821-aa22-b8802720062bInstanceRelationTypeClassType", "d8a0bd91-94be-42cf-9c4b-0cd3dac59577InstanceRelationTypeClassType", "6c32824b-9fa0-4fdb-9b13-52667523e750InstanceRelationTypeClassType", "44ea4d45-85fa-4664-90f9-0eb5d1c099afInstanceRelationTypeClassType", "8b9e44f2-56d5-4281-9ac6-b2f9cde9a3eeInstanceRelationTypeClassType", "0e530723-0d91-4aa7-b06d-bdb11f799a21InstanceRelationTypeClassType", "fb0dedc8-9a78-40ed-aeeb-68fedbf80514InstanceRelationTypeClassType", "70825c69-3877-4b06-bddb-8ec0d542a232InstanceRelationTypeClassType", "311882d1-cbb3-4042-a748-153eaa877c6dInstanceRelationTypeClassType", "0f7a5b42-68bf-455b-a24b-5e2df8c3233bInstanceRelationTypeClassType", "81bdfd5b-ac15-413d-9822-8fcc3935cc26InstanceRelationTypeClassType", "d03d42a5-9952-4856-9140-119219018950InstanceRelationTypeClassType", "91cbf027-adb5-46bf-b122-2b19b3bd6b14InstanceRelationTypeClassType", "1beec632-7803-4ab5-acc0-724fef3a42a3InstanceRelationTypeClassType", "3f93f118-54e6-4855-abb4-f3e546e1a789InstanceRelationTypeClassType", "8fc0e586-a1dc-490c-b27e-d9483a2403b1InstanceRelationTypeClassType", "4891911d-d293-4633-9cbe-20726ff050a1InstanceRelationTypeClassType", "d566632b-5147-4f5c-b328-43b93b552b27InstanceRelationTypeClassType", "da0f1189-69c0-41a3-940a-834e69277460InstanceRelationTypeClassType", "4ed722d7-379c-4ec4-bdbb-308b6e267b57InstanceRelationTypeClassType", "7f721c5c-4bb9-4b38-8b4b-f8436ee61abdInstanceRelationTypeClassType", "8cb5b366-ee19-4395-9f8e-8a6acbd1bf31InstanceRelationTypeClassType", "97567353-6c08-4ab5-89f4-31cced3b9933InstanceRelationTypeClassType", "5c2e7f66-fe9e-44de-9f2b-8badad34f90fInstanceRelationTypeClassType", "b33d0e4f-0c72-441e-8208-11b9a7e09cfdInstanceRelationTypeClassType", "d2c9b9dd-5a00-4e57-bb51-e5bc20a3d72bInstanceRelationTypeClassType", "433150dc-68c9-42dc-b155-43c23a59f402InstanceRelationTypeClassType", "3d7adf87-2727-4579-8d00-93fe2f6c379dInstanceRelationTypeClassType", "d11f2835-3c6b-41ff-83f3-e6b7d473b1e3InstanceRelationTypeClassType", "ad76b237-6813-49e9-936d-5cee332e253bInstanceRelationTypeClassType", "4f0b1386-7bbd-4d62-a2f8-09eeaaee6061InstanceRelationTypeClassType", "ad5bdc80-00f3-4bc6-be7c-e3efc73f6958InstanceRelationTypeClassType", "bb84b749-042f-40a4-adfe-36c93b22ea46InstanceRelationTypeClassType", "12967b9d-ef9b-4dfb-867c-c7b50203a588InstanceRelationTypeClassType", "ac5479bd-e75d-47b6-bdec-71b36bf8a714InstanceRelationTypeClassType", "312dfe7f-83da-40c3-944e-9cac78a1a67dInstanceRelationTypeClassType", "4419d7ce-7132-439a-860d-7266b03943b8InstanceRelationTypeClassType", "438a8ab4-3d0a-442e-833b-74ae2e1827a4InstanceRelationTypeClassType", "429e0d41-5368-4244-8b93-c3b4fd5db02dInstanceRelationTypeClassType", "bac332e9-ddad-4352-979c-68ff759015f4InstanceRelationTypeClassType", "163d97fd-c996-47ad-85c7-bf61a55589d8InstanceRelationTypeClassType", "ba05e762-67ed-40b0-8128-a2d029804848InstanceRelationTypeClassType", "74575f85-ccc0-4333-abb8-c1d501f532aeInstanceRelationTypeClassType", "4d9f55d7-be5b-4e60-8bfc-6c694f1d924dInstanceRelationTypeClassType", "d4dd50d2-9d23-4269-b439-c757492d6deaInstanceRelationTypeClassType", "6fbad6b9-e073-4ab3-81ef-52f1903cea54InstanceRelationTypeClassType", "0d2ca5f9-7b82-4efe-b012-20b5fede380eInstanceRelationTypeClassType", "6f098aa4-284a-4298-9ac4-f51fd7c0be1cInstanceRelationTypeClassType", "71e62108-6a7c-4df0-885a-fa747c23696cInstanceRelationTypeClassType", "523f9838-6c27-45eb-93a7-e3946e382cc5InstanceRelationTypeClassType", "703b0285-5f81-4925-ae67-c0c27e344a63InstanceRelationTypeClassType", "c939c466-c36f-402b-89e5-d7108fd01640InstanceRelationTypeClassType", "9e6fe72f-2b95-4fad-9150-e989f0e5945cInstanceRelationTypeClassType", "e8cbc8f0-7ae2-4b08-be68-f66a450ed3b0InstanceRelationTypeClassType", "95e16a0e-fb55-4387-8df8-6ed7587fd15dInstanceRelationTypeClassType", "292e1b20-f0fd-45a3-96b2-dbdb9a465361InstanceRelationTypeClassType", "edd99a10-3301-4615-8c40-6c29f5299349InstanceRelationTypeClassType", "c91e5fda-b6ac-4e97-9e4e-74a0d83c37f5InstanceRelationTypeClassType", "c408d737-3f3b-4946-9d7e-e4584e718960InstanceRelationTypeClassType", "5b24cf49-9569-4b6a-a05c-3692f41f52dcInstanceRelationTypeClassType", "7785393f-cd68-4f48-8fc9-e0555b488ca2InstanceRelationTypeClassType", "0ae6360e-5ee6-40a6-ac60-8b2a0d3fb8e6InstanceRelationTypeClassType", "57586831-1a0c-462f-8dcf-ec5f9ec54729InstanceRelationTypeClassType", "62e50e6d-d5ba-4665-9780-9be21dd88b73InstanceRelationTypeClassType", "5a6c4d87-45e6-46f3-b89c-e8866a47a104InstanceRelationTypeClassType", "93305315-4268-4a9a-bdf3-46f078814ebfInstanceRelationTypeClassType", "62c25d49-1389-49eb-9030-39154a445c8fInstanceRelationTypeClassType", "bd149126-0400-410d-90e2-553310de0354InstanceRelationTypeClassType", "805ba32f-1ce1-415a-b791-956bee0e66caInstanceRelationTypeClassType", "f9463b7d-162e-48e1-8e28-95feb07afe7aInstanceRelationTypeClassType", "4301d916-463c-4efc-98fa-30bf09227d98InstanceRelationTypeClassType", "f0999425-db98-4963-aab2-979eaaf11752InstanceRelationTypeClassType", "59383f1a-886a-4ffd-88b5-72be0c3ec19bInstanceRelationTypeClassType", "e81da093-925e-46fc-802a-e9eaea060576InstanceRelationTypeClassType", "3940a828-fc9d-4d84-8ddc-c055040c93c3InstanceRelationTypeClassType", "43ed62f6-99fd-4bcf-aa1d-857372f052e0InstanceRelationTypeClassType", "849728cf-f3fd-4b79-85de-e4b71bfd2ed9InstanceRelationTypeClassType", "a72f0820-1e17-492f-b420-850a6498dde5InstanceRelationTypeClassType", "634d47de-686f-4f0b-a876-e4fab6fc1ed5InstanceRelationTypeClassType", "932855ad-f285-4cde-b199-70e76e8cfe56InstanceRelationTypeClassType", "a402433f-9939-4fa9-9d13-7723949f3fabInstanceRelationTypeClassType", "c2e42a8b-7d36-4714-984d-167f6a4ecd6aInstanceRelationTypeClassType", "2e6cc8c2-f1d9-46e1-863b-581f6905afe7InstanceRelationTypeClassType", "51f19315-2be6-4d74-b422-f1175917184dInstanceRelationTypeClassType", "95feefa5-362d-4877-9902-581e4d577221InstanceRelationTypeClassType", "2d8b44b6-a818-4c6b-b33b-46bf103435a7InstanceRelationTypeClassType", "b314a60a-e4e0-45d2-8fea-0d48a6f48ae4InstanceRelationTypeClassType", "34be39e6-75ee-48ef-a8d6-d3b4c34b0b39InstanceRelationTypeClassType", "5b66c4a1-5070-41b7-9520-05e91da1c14dInstanceRelationTypeClassType", "626cb959-fec8-4e5d-876e-398f023b73fcInstanceRelationTypeClassType", "2986d9fc-466c-4b88-b97e-955f2826093bInstanceRelationTypeClassType", "b9b729a2-ea5b-442b-b7b7-45a3a611832dInstanceRelationTypeClassType", "7e74d234-e379-44c2-b49e-d760a6ff0b5cInstanceRelationTypeClassType", "52008c26-b669-491b-8fcb-fe455e4f9b09InstanceRelationTypeClassType", "5ec3257a-b159-4c2a-af44-de6360137312InstanceRelationTypeClassType", "1c0769e7-5638-487a-9caf-bb052978d6c0InstanceRelationTypeClassType", "b5417575-abae-4e0b-a681-6d74357f7c76InstanceRelationTypeClassType", "13a7b026-dd35-4242-b860-97d90348662dInstanceRelationTypeClassType", "eddc7027-dcb5-476a-a010-f862327c67fcInstanceRelationTypeClassType", "6a01b73c-9013-4d7e-b115-bd1aa8184499InstanceRelationTypeClassType", "917dd0cb-b445-46c2-b865-64489c1ed76bInstanceRelationTypeClassType", "b185cd72-5cce-4544-9829-550282c9e96aInstanceRelationTypeClassType", "c9c189df-b5ed-48ce-9b96-0416cc522ccbInstanceRelationTypeClassType", "fb34664f-22ee-4d62-a69e-49d74cf688d3InstanceRelationTypeClassType", "1a5f6912-160a-4fe8-b3ca-67178cf41042InstanceRelationTypeClassType", "80b2b252-1862-4a42-912c-bb057fbb8e45InstanceRelationTypeClassType"],
		"isLiv": true,
		"node_type": null,
		"isVrt": false,
		"sbOf": ["TypeType"],
		"lox": "ClassType",
		"isPrv": false,
		"details": {
			"en": ["Topic Map upper Class type"]
		}
	}
}
 */
