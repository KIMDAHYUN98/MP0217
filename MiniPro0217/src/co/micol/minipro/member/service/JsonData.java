package co.micol.minipro.member.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.micol.minipro.common.Service;

public class JsonData implements Service {

	@Override
	public String run(HttpServletRequest request, HttpServletResponse response) {
		String json = "{\"id\":\"hong\", \"name\":\"hwang\", \"age\":15}";
		request.setAttribute("json", json);
		return "views/main/json.jsp";
	}

}
