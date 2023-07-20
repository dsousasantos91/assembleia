package br.com.dsousasantos91.assembleia;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/")
public class RedirectSwagger {
    @GetMapping
    public RedirectView redirectSwaggeerDocumentation() {
        return new RedirectView("/swagger-ui/index.html");
    }
}
