package edu.pucp.gtics.lab5_gtics_20211.controller;

import edu.pucp.gtics.lab5_gtics_20211.entity.Juegos;
import edu.pucp.gtics.lab5_gtics_20211.entity.JuegosUserDto;
import edu.pucp.gtics.lab5_gtics_20211.entity.Plataformas;
import edu.pucp.gtics.lab5_gtics_20211.entity.User;
import edu.pucp.gtics.lab5_gtics_20211.repository.JuegosRepository;
import edu.pucp.gtics.lab5_gtics_20211.repository.PlataformasRepository;
import edu.pucp.gtics.lab5_gtics_20211.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.List;
@Controller
public class JuegosController {

    @Autowired
    PlataformasRepository plataformasRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    JuegosRepository juegosRepository;

    @GetMapping("/juegos/lista")
    public String listaJuegos ( Model model, HttpSession httpSession ){
        User usuario = (User) httpSession.getAttribute("usuario");
        if(usuario.getAutorizacion().equals("ADMIN")){
            model.addAttribute("listaJuegos", juegosRepository.listarJuegos());
            return "/juegos/lista";
        }else{
            List<JuegosUserDto> lista = juegosRepository.obtenerJuegosPorUser(usuario.getIdusuario());
            model.addAttribute("listaJuegosUsuario", juegosRepository.obtenerJuegosPorUser(usuario.getIdusuario()));
            return "/juegos/comprado";
        }

    }

    @GetMapping(value = {"", "/", "/vista"})
    public String vistaJuegos (Model model){
        model.addAttribute("listaJuegos", juegosRepository.listaJuegosOrdenadosPorNombreDesc());
        return "/juegos/vista";
    }

    @GetMapping("/juegos/nuevo")
    public String nuevoJuegos(Model model, @ModelAttribute("juego") Juegos juego){
        model.addAttribute("listaPlataformas", plataformasRepository.findAll());
        return "juegos/editarFrm";

    }

    @GetMapping("/juegos/editar")
    public String editarJuegos(@RequestParam("id") int id, Model model){

        Optional<Juegos> optionalJuegos = juegosRepository.findById(id);

        if (optionalJuegos.isPresent()) {
            model.addAttribute("juego", optionalJuegos.get());
            model.addAttribute("listaPlataformas", plataformasRepository.findAll());
            return "juegos/editarFrm";
        } else {
            return "redirect:/juegos/vista";
        }

    }

    @PostMapping( "/juegos/guardar" )
    public String guardarJuegos(Model model, RedirectAttributes attr, @ModelAttribute("juego") @Valid Juegos juego, BindingResult bindingResult ){

        Boolean errorPlat = false;
        Boolean errorImag = false;
        if(juego.getPlataforma().getIdplataforma()==-1){
            errorPlat=true;
        }
        if(juego.getImage().equals("")){
            errorImag=true;
        }
        if (bindingResult.hasErrors() || errorPlat || errorImag) {
            model.addAttribute("listaPlataformas", plataformasRepository.findAll());
            if(errorPlat){
                model.addAttribute("msg1", "Debe seleccionar una plataforma");
            }
            if (errorImag){
                model.addAttribute("msg2","Debe adjuntar el url de una imagen");
            }
            return "juegos/editarFrm";
        } else {

            if (juego.getIdjuego() == 0) {
                attr.addFlashAttribute("msg", "Juego creado exitosamente");
            } else {
                attr.addFlashAttribute("msg", "Juego actualizado exitosamente");
            }
            juegosRepository.save(juego);
            return "redirect:/juegos/lista";
        }

    }

    @GetMapping("/juegos/borrar")
    public String borrarDistribuidora(@RequestParam("id") int id){
        Optional<Juegos> opt = juegosRepository.findById(id);
        if (opt.isPresent()) {
            juegosRepository.deleteById(id);
        }
        return "redirect:/juegos/lista";
    }

}
