package ru.protei.portal.test.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@RequestMapping(value = "/test-api", headers = "Accept=application/json")
@EnableWebMvc
public class PortalTestApiController {
}
