package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.controller;

import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service.KebunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/kebun")
public class KebunController {

    @Autowired
    private KebunService service;

    @GetMapping("/create")
    public String createKebunPage(Model model) {
        Kebun kebun = new Kebun();
        model.addAttribute("kebun", kebun);
        return "createKebun";
    }

    @PostMapping("/create")
    public String createKebunPost(@ModelAttribute Kebun kebun, Model model) {
        kebun.setId(UUID.randomUUID());
        service.create(kebun);
        return "redirect:list";
    }

    @GetMapping("/list")
    public String kebunListPage(Model model) {
        List<Kebun> allKebun = service.findAll();
        model.addAttribute("kebun", allKebun);
        return "kebunList";
    }

    @GetMapping("/edit/{kebunId}")
    public String editKebunPage(@PathVariable("kebunId") UUID kebunId, Model model) {
        Kebun kebun = service.findById(kebunId).orElse(null);
        model.addAttribute("kebun", kebun);
        return "editKebun";
    }

    @PostMapping("/edit")
    public String editKebunPost(@ModelAttribute Kebun kebun) {
        service.update(kebun.getId(), kebun);
        return "redirect:list";
    }

    @GetMapping("/delete/{kebunId}")
    public String deleteKebun(@PathVariable("kebunId") UUID kebunId) {
        service.delete(kebunId);
        return "redirect:../list";
    }
}
