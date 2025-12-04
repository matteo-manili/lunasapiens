package com.lunasapiens.controller;

import com.lunasapiens.Constants;
import com.lunasapiens.utils.Utils;
import com.lunasapiens.repository.VideoChunksRepository;
import com.lunasapiens.service.RAGIAService;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
public class PsicologoController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PsicologoController.class);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private VideoChunksRepository videoChunksRepository;



    // #################################### PSICOLOGO #####################################

    /**
     * servizio tema natale
     */
    @GetMapping("/psicologo")
    public String psicologo(Model model,
                             @ModelAttribute("psicologoDescIstruzioniBOTSystem") String psicologoDescIstruzioniBOTSystem,
                             @ModelAttribute("paginaChatId") String paginaChatId,
                             @ModelAttribute(Constants.USER_SESSION_ID) String userSessionId,
                             @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("sono in psicologo");
        if( userDetails != null ){
            logger.info("userDetails: "+userDetails.getUsername());
        }

        Optional.ofNullable(psicologoDescIstruzioniBOTSystem).filter(descriptionBOTSystem -> !descriptionBOTSystem.isEmpty()).ifPresent(descriptionBOTSystem -> model.addAttribute("psicologoDescIstruzioniBOTSystem", descriptionBOTSystem));
        Optional.ofNullable(paginaChatId).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute("paginaChatId", id));
        Optional.ofNullable(userSessionId).filter(id -> !id.isEmpty()).ifPresent(id -> model.addAttribute(Constants.USER_SESSION_ID, id));

        model.addAttribute("MAX_MESSAGES_PER_DAY_UTENTE", Constants.MAX_MESSAGES_PER_DAY_UTENTE);
        model.addAttribute("MAX_MESSAGES_PER_DAY_ANONYMOUS", Constants.MAX_MESSAGES_PER_DAY_ANONYMOUS);

        return "psicologo";
    }


    @GetMapping("/psicologoSubmit")
    public String psicologoSubmit(
            RedirectAttributes redirectAttributes, HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("sono in psicologoSubmit");

        HttpSession session = request.getSession(true); // Crea una nuova sessione se non esiste
        String userId = (String) session.getAttribute(Constants.USER_SESSION_ID);
        if (userId == null) {
            userId = UUID.randomUUID().toString(); // Genera un nuovo ID solo se non esiste
            session.setAttribute(Constants.USER_SESSION_ID, userId);
        }
        logger.info(Constants.USER_SESSION_ID + userId);

        String paginaChatId = UUID.randomUUID().toString();
        redirectAttributes.addFlashAttribute("paginaChatId", paginaChatId);
        logger.info("paginaChatId: " + paginaChatId);

        // Metto in cache i chatMessageIa
        Cache cache = cacheManager.getCache(Constants.MESSAGE_BOT_CACHE);
        if (cache == null) {
            logger.error("Cache not found: " + Constants.MESSAGE_BOT_CACHE);
            return "redirect:/psicologo";
        }

        StringBuilder psicologoDescIstruzioniBOTSystem = RAGIAService.psicologoIstruzioneBOTSystem();
        if( userDetails != null && (userDetails.getUsername().equals(Constants.MATTEO_MANILI_GMAIL)) ){
            redirectAttributes.addFlashAttribute("psicologoDescIstruzioniBOTSystem",
                    Utils.convertPlainTextToHtml(psicologoDescIstruzioniBOTSystem.toString())
                            + "<br>" + dammiTitoliVideo(" | ")
            );
        }
        List<ChatMessage> chatMessageIa = new ArrayList<>();
        chatMessageIa.add(new ChatMessage("system", psicologoDescIstruzioniBOTSystem.toString() ));
        cache.put(paginaChatId, chatMessageIa);

        redirectAttributes.addFlashAttribute(Constants.USER_SESSION_ID, userId);
        redirectAttributes.addFlashAttribute("avviaChatPsicologo", "AVVIA CHAT PSICOLOGO");

        return "redirect:/psicologo";
    }


    public String dammiTitoliVideo(String separatore) {
        List<String> list = videoChunksRepository.findAllTitles();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(separatore);
            }
        }
        return sb.toString();
    }






}
