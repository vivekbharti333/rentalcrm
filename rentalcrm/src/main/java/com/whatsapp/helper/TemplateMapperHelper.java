package com.whatsapp.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.datfusrental.constant.Constant;
import com.datfusrental.exceptions.BizException;
import com.datfusrental.whatsapp.request.TemplateBodyVariableRequest;
import com.datfusrental.whatsapp.request.TemplateRequestObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class TemplateMapperHelper {
	
	public void validateTemplateRequest(TemplateRequestObject templateRequestObject) throws BizException {
		if (templateRequestObject == null) {
			throw new BizException(Constant.BAD_REQUEST_CODE, "Bad Request Object Null");
		}
	}


	private static final String LIST_TEMPLATE_URL = "https://graph.facebook.com/v24.0/1249368524920051/message_templates";
	private static final String SINGLE_TEMPLATE_BASE_URL = "https://graph.facebook.com/v24.0/";

	/**
	 * 🔑 MAIN METHOD Uses requestFor from TemplateRequestObject
	 */
	public List<TemplateRequestObject> getTemplates(TemplateRequestObject request) throws IOException {

		String jsonResponse = fetchTemplatesFromWhatsApp(request);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(jsonResponse);

		// SAME mapper for BOTH ALL & BYID
		return mapDataNode(rootNode);
	}

	/**
	 * Calls WhatsApp API based on requestFor
	 */
	private String fetchTemplatesFromWhatsApp(TemplateRequestObject request) throws IOException {

		if (request.getRequestFor() == null) {
			throw new IllegalArgumentException("requestFor is required");
		}

		String url;

		if ("ALL".equalsIgnoreCase(request.getRequestFor())) {

			url = LIST_TEMPLATE_URL;

		} else if ("BY_ID".equalsIgnoreCase(request.getRequestFor())) {

			if (request.getTemplateId() == null || request.getTemplateId().isBlank()) {
				throw new IllegalArgumentException("templateId is required when requestFor = BYID");
			}

			url = SINGLE_TEMPLATE_BASE_URL + request.getTemplateId();

		} else if ("BY_NAME".equalsIgnoreCase(request.getRequestFor())) {

			if (request.getTemplateName() == null || request.getTemplateName().isBlank()) {
				throw new IllegalArgumentException("template name is required when requestFor = BYNAME");
			}

			url = SINGLE_TEMPLATE_BASE_URL+ "2008532549765503/message_templates?name=" +request.getTemplateName();

		} else {
			throw new IllegalArgumentException("Invalid requestFor value: " + request.getRequestFor());
		}

		OkHttpClient client = new OkHttpClient();

		Request httpRequest = new Request.Builder().url(url).get().addHeader("Authorization", "Bearer " + Constant.ACCESS_TOKEN)
				.build();

		try (Response response = client.newCall(httpRequest).execute()) {

			// 🔥 READ BODY ONCE FOR BOTH SUCCESS & ERROR
			String responseBody = response.body() != null ? response.body().string() : "";

			if (!response.isSuccessful()) {
				throw new IOException("WhatsApp API error: HTTP " + response.code() + " | Response: " + responseBody);
			}

			return responseBody;
		}
	}

	/**
	 * 🔁 UNIVERSAL MAPPER Handles: - data[] (ALL) - single object (BYID)
	 */
	private List<TemplateRequestObject> mapDataNode(JsonNode rootNode) {

		List<TemplateRequestObject> result = new ArrayList<>();

		if (rootNode == null || rootNode.isMissingNode()) {
			return result;
		}

		List<JsonNode> templateNodes = new ArrayList<>();

		if (rootNode.has("data") && rootNode.get("data").isArray()) {
			rootNode.get("data").forEach(templateNodes::add);
		} else if (rootNode.isObject()) {
			templateNodes.add(rootNode);
		}

		for (JsonNode templateNode : templateNodes) {

			TemplateRequestObject obj = new TemplateRequestObject();

			obj.setTemplateId(templateNode.path("id").asText());
			obj.setTemplateName(templateNode.path("name").asText());
			obj.setParameterFormat(templateNode.path("parameter_format").asText());
			obj.setLanguage(templateNode.path("language").asText());
			obj.setStatus(templateNode.path("status").asText());
			obj.setCategory(templateNode.path("category").asText());
			obj.setSub_category(templateNode.path("sub_category").asText());

			List<TemplateBodyVariableRequest> variables = new ArrayList<>();

			for (JsonNode component : templateNode.path("components")) {

				String type = component.path("type").asText();

				switch (type) {

				case "HEADER":
					obj.setHeaderFormat(component.path("format").asText());
					obj.setHeaderText(component.path("text").asText());
					break;

				case "BODY":
					obj.setMsgBodyText(component.path("text").asText());

					JsonNode bodyText = component.path("example").path("body_text");

					if (bodyText.isArray() && bodyText.size() > 0) {
						for (JsonNode val : bodyText.get(0)) {
							TemplateBodyVariableRequest v = new TemplateBodyVariableRequest();
							v.setBodyVariable(val.asText());
							variables.add(v);
						}
					}
					break;

				case "FOOTER":
					obj.setFooterText(component.path("text").asText());
					break;
				}
			}

			obj.setMsgBodyVariable(variables);
			result.add(obj);
		}

		return result;
	}
	
	
	public String deleteTemplate(String templateName) throws Exception {

	    OkHttpClient client = new OkHttpClient();

	    String encodedTemplateName = java.net.URLEncoder.encode(templateName, java.nio.charset.StandardCharsets.UTF_8);

//	    String url = "https://graph.facebook.com/v24.0/" + wabaId +"/message_templates?name=" + encodedTemplateName;
	    String url = Constant.WHATS_APP_BASE_URL+"v24.0/1249368524920051/message_templates?name=" + encodedTemplateName;

	    RequestBody body = RequestBody.create("", MediaType.parse("text/plain"));

	    Request request = new Request.Builder()
	            .url(url)
	            .delete(body)
	            .addHeader("Authorization", "Bearer " + Constant.ACCESS_TOKEN)
	            .build();

	    Response response = client.newCall(request).execute();

	    String responseBody = response.body() != null ? response.body().string() : null;

	    System.out.println("Status Code: " + response.code());
	    System.out.println("Raw Response: " + responseBody);

	    

	    return responseBody;
	}
}