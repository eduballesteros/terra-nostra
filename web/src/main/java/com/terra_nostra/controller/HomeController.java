package com.terra_nostra.controller;


import com.terra_nostra.dto.UsuarioDto;
import com.terra_nostra.service.UsuarioService;
import com.terra_nostra.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller

/**
 * Controlador principal para manejar la navegación de la aplicación.
 * Proporciona rutas para la página principal, la tienda, el panel de administración
 * y el panel de usuario.
 *
 * @author ebp
 * @version 1.0
 */

public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);


    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Muestra la página de inicio.*
     * @return Nombre de la vista `index`.
     */


    @GetMapping("/terra-nostra")
    public String home() {
        return "index";
    }

    /**
     * Muestra la página de la tienda.*
     * @return `ModelAndView` con la vista `shop`.
     */


    @GetMapping("/tienda")
    public ModelAndView mostrarTienda() {
        return new ModelAndView("tienda");
    }

    @GetMapping("/sobre-nosotros")
    public ModelAndView mostrarSobreNosotros() {
        return new ModelAndView("sobre-nosotros");
    }

    /**
     * Muestra el panel de administración si el usuario tiene el rol adecuado.
     * Verifica el token almacenado en las cookies para determinar si el usuario es ADMIN.
     * Si el token es válido, obtiene la lista de usuarios desde la API.
     * @param request Objeto `HttpServletRequest` para acceder a las cookies.
     * @return `ModelAndView` con la vista de administración o una redirección en caso de error.
     */


    @GetMapping("/admin")
    public ModelAndView panelAdmin(HttpServletRequest request) {
        logger.info("📢 Iniciando carga de la página de administración...");

        ModelAndView mav = new ModelAndView("admin");

        // Verificar el token desde las cookies
        String token = obtenerTokenDesdeCookies(request);

        if (token == null || !jwtUtil.isTokenValido(token)) {
            logger.warn("⚠ Token no válido o inexistente. Redirigiendo a error.");
            mav.setViewName("error");
        } else {
            String usuarioLogueado = jwtUtil.obtenerEmailDesdeToken(token);
            String rolUsuario = jwtUtil.obtenerRolDesdeToken(token);

            if ("ROLE_ADMIN".equals(rolUsuario)) {
                logger.info("✅ Usuario autenticado como ADMIN: {}", usuarioLogueado);
                mav.addObject("usuarioLogueado", usuarioLogueado);

                List<UsuarioDto> usuarios = usuarioService.obtenerTodosLosUsuarios();

                if (usuarios == null || usuarios.isEmpty()) {
                    logger.warn("⚠ No se encontraron usuarios en la API.");
                } else {
                    logger.info("✅ Usuarios obtenidos con éxito: {} registros.", usuarios.size());
                }

                mav.addObject("usuarios", usuarios);
            } else {
                logger.warn("⛔ Acceso denegado para usuario: {} (No es ADMIN)", usuarioLogueado);
                mav.setViewName("redirect:/error");
            }
        }

        return mav;
    }

    /**
     * Muestra el panel de usuario si el usuario tiene el rol adecuado.
     * Verifica el token almacenado en las cookies para determinar si el usuario es USER.*
     * @param request Objeto `HttpServletRequest` para acceder a las cookies.
     * @return `ModelAndView` con la vista de usuario o una redirección en caso de error.
     */


    @GetMapping("/infoUser")
    public ModelAndView panelUsuario(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("infoUser");
        String token = obtenerTokenDesdeCookies(request);

        if (token == null || !jwtUtil.isTokenValido(token)) {
            mav.setViewName("error");
        } else {
            String usuarioLogueado = jwtUtil.obtenerEmailDesdeToken(token);
            String rolUsuario = jwtUtil.obtenerRolDesdeToken(token);

            if ("ROLE_USER".equals(rolUsuario)) {
                mav.addObject("usuarioLogueado", usuarioLogueado);
            } else {
                mav.setViewName("redirect:/error");
            }
        }

        return mav;
    }

    /**
     * Muestra la página de error.*
     * @return `ModelAndView` con la vista `error`.
     */


    @GetMapping("/error")
    public ModelAndView errorPage() {
        return new ModelAndView("error");
    }

    private String obtenerTokenDesdeCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @GetMapping("/producto/{nombre}")
    public ModelAndView mostrarProductoPorNombre(@PathVariable String nombre, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("producto");

        // Convertimos el slug a nombre normal: "vitamina-c-natural" → "Vitamina C Natural"
        String nombreFormateado = nombre.replace("-", " ");
        mav.addObject("nombreProducto", nombreFormateado);

        String token = obtenerTokenDesdeCookies(request);
        if (token != null && jwtUtil.isTokenValido(token)) {
            Long usuarioId = jwtUtil.obtenerUsuarioIdDesdeToken(token);
            mav.addObject("usuarioId", usuarioId);
        }

        return mav;
    }


    @GetMapping("/carrito")
    public ModelAndView mostrarCarrito(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("carrito");

        String token = obtenerTokenDesdeCookies(request);
        if (token != null && jwtUtil.isTokenValido(token)) {
            Long usuarioId = jwtUtil.obtenerUsuarioIdDesdeToken(token);
            boolean verificado = jwtUtil.obtenerVerificacionCorreoDesdeToken(token);

            mav.addObject("usuarioId", usuarioId);
            mav.addObject("correoVerificado", verificado);

        }

        return mav;
    }

    @GetMapping("/resumenPedido")
    public ModelAndView mostrarCompra(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("resumenPedido");
        String token = obtenerTokenDesdeCookies(request);
        if (token != null && jwtUtil.isTokenValido(token)) {
            Long usuarioId = jwtUtil.obtenerUsuarioIdDesdeToken(token);
            boolean verificado = jwtUtil.obtenerVerificacionCorreoDesdeToken(token);
            mav.addObject("usuarioId", usuarioId);
            mav.addObject("correoVerificado", verificado);
        }
        return mav;
    }



    @GetMapping("/cambiar-contrasenia")
    public ModelAndView mostrarFormularioCambio(@RequestParam("token") String token) {
        ModelAndView mav;

        // Validar si el token existe y es válido
        boolean tokenValido = usuarioService.validarTokenRecuperacion(token); // Este método debes implementarlo si aún no existe

        if (!tokenValido) {
            mav = new ModelAndView("error");
        } else {
            mav = new ModelAndView("cambiar-contrasenia");
            mav.addObject("token", token);
        }

        return mav;
    }

}

