package org.zerock.b01.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.PlaceDTO;
import org.zerock.b01.dto.PlaceListAllDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.service.PlaceService;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/place")
@Log4j2
@RequiredArgsConstructor
public class PlaceController {

    @Value("${org.zerock.upload.path}")// import 시에 springframework으로 시작하는 Value
    private String uploadPath;

    private final PlaceService placeService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){

        //PageResponseDTO<PlaceDTO> responseDTO = placeService.list(pageRequestDTO);

        PageResponseDTO<PlaceListAllDTO> responseDTO =
                placeService.listWithAll(pageRequestDTO);

        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping("/main")
    public void mainGET(){

    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/register")
    public void registerGET(){

    }

    @PostMapping("/register")
    public String registerPost(@Valid PlaceDTO placeDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){

        log.info("place POST register.......");

        if(bindingResult.hasErrors()) {
            log.info("has errors.......");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );
            return "redirect:/place/register";
        }

        log.info(placeDTO);

        Long bno  = placeService.register(placeDTO);

        redirectAttributes.addFlashAttribute("result", bno);

        return "redirect:/place/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping({"/read", "/modify"})
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model){

        PlaceDTO placeDTO = placeService.readOne(bno);

        log.info(placeDTO);

        model.addAttribute("dto", placeDTO);

    }

    @PreAuthorize("principal.username == #placeDTO.writer")
    @PostMapping("/modify")
    public String modify( @Valid PlaceDTO placeDTO,
                          BindingResult bindingResult,
                          PageRequestDTO pageRequestDTO,
                          RedirectAttributes redirectAttributes){


        log.info("place modify post......." + placeDTO);

        if(bindingResult.hasErrors()) {
            log.info("has errors.......");

            String link = pageRequestDTO.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );

            redirectAttributes.addAttribute("bno", placeDTO.getBno());

            return "redirect:/place/modify?"+link;
        }

        placeService.modify(placeDTO);

        redirectAttributes.addFlashAttribute("result", "modified");

        redirectAttributes.addAttribute("bno", placeDTO.getBno());

        return "redirect:/place/read";
    }

    @PreAuthorize("principal.username == #placeDTO.writer")
    @PostMapping("/remove")
    public String remove(PlaceDTO placeDTO, RedirectAttributes redirectAttributes) {


        Long bno  = placeDTO.getBno();
        log.info("remove post.. " + bno);

        placeService.remove(bno);

        //게시물이 삭제되었다면 첨부 파일 삭제
        log.info(placeDTO.getFileNames());
        List<String> fileNames = placeDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            removeFiles(fileNames);
        }

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/place/list";

    }


    public void removeFiles(List<String> files){

        for (String fileName:files) {

            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();


            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                //섬네일이 존재한다면
                if (contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }//end for
    }
}
