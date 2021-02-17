package co.micol.minipro.member.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.micol.minipro.common.Service;

public class Test implements Service {

	@Override
	public String run(HttpServletRequest request, HttpServletResponse response) {
		return "forward.do";
	}

}
