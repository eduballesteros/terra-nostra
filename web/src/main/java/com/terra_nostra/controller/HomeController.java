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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controlador principal para la navegación del frontend de Terra Nostra.
 *
 * Gestiona las vistas de:
 * - Inicio
 * - Tienda
 * - Sobre Nosotros
 * - Panel de administración (admin)
 * - Panel del usuario (infoUser)
 * - Carrito, resumen de compra, y detalles de productos
 *
 * Valida la sesión del usuario mediante JWT almacenado en cookies.
 * Determina el acceso a cada sección en función del rol del usuario.
 *
 * @author ebp
 * @version 1.0
 */
@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Muestra la página de inicio.
     *
     * @return nombre de la vista `index.jsp`.
     */
    @GetMapping("/terra-nostra")
    public String home() {
        return "index";
    }

    /**
     * Muestra la vista de la tienda.
     *
     * @return `ModelAndView` con la vista `tienda.jsp`.
     */
    @GetMapping("/tienda")
    public ModelAndView mostrarTienda() {
        return new ModelAndView("tienda");
    }

    /**
     * Muestra la vista de "Sobre nosotros".
     *
     * @return `ModelAndView` con la vista `sobre-nosotros.jsp`.
     */
    @GetMapping("/sobre-nosotros")
    public ModelAndView mostrarSobreNosotros() {
        return new ModelAndView("sobre-nosotros");
    }

    /**
     * Muestra el panel de administración si el usuario tiene el rol `ADMIN`.
     *
     * Extrae el token de la cookie `SESSIONID` y valida su rol.
     * Carga la lista de usuarios desde la API si la verificación es correcta.
     *
     * @param request Petición HTTP para obtener las cookies.
     * @return `ModelAndView` con la vista `admin.jsp` o redirección a `error.jsp`.
     */
    @GetMapping("/admin")
    public ModelAndView panelAdmin(HttpServletRequest request) {
        logger.info("📢 Iniciando carga de la página de administración...");
        ModelAndView mav = new ModelAndView("admin");

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
                mav.addObject("usuarios", usuarios);
            } else {
                logger.warn("⛔ Acceso denegado para usuario: {} (No es ADMIN)", usuarioLogueado);
                mav.setViewName("redirect:/error");
            }
        }

        return mav;
    }

    /**
     * Muestra el panel de usuario si el rol es `USER`.
     *
     * @param request Petición HTTP para acceder al token de sesión.
     * @return `ModelAndView` con la vista `infoUser.jsp` o `error.jsp` si no está autorizado.
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
     * Devuelve la vista de error general.
     *
     * @return `ModelAndView` con la vista `error.jsp`.
     */
    @GetMapping("/error")
    public ModelAndView errorPage() {
        return new ModelAndView("error");
    }

    /**
     * Muestra los detalles de un producto concreto a partir de su nombre en formato slug.
     *
     * Si el usuario está autenticado, se añade su ID como atributo a la vista.
     *
     * @param nombre Slug del nombre del producto (ej. "miel-de-abeja").
     * @param request Petición para extraer token de sesión.
     * @return `ModelAndView` con vista `producto.jsp`.
     */
    @GetMapping("/producto/{nombre}")
    public ModelAndView mostrarProductoPorNombre(@PathVariable String nombre, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("producto");

        String nombreFormateado = nombre.replace("-", " ");
        mav.addObject("nombreProducto", nombreFormateado);

        String token = obtenerTokenDesdeCookies(request);
        if (token != null && jwtUtil.isTokenValido(token)) {
            Long usuarioId = jwtUtil.obtenerUsuarioIdDesdeToken(token);
            mav.addObject("usuarioId", usuarioId);
        }

        return mav;
    }

    /**
     * Muestra el carrito lateral del usuario si está autenticado.
     * Añade su ID y si tiene correo verificado.
     *
     * @param request Petición HTTP para acceder al token JWT en cookies.
     * @return `ModelAndView` con la vista `carrito.jsp`.
     */
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

    /**
     * Muestra la vista de resumen de pedido justo antes de la confirmación final.
     *
     * @param request Petición HTTP con cookie de sesión.
     * @return `ModelAndView` con la vista `resumenPedido.jsp`.
     */
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

    /**
     * Muestra el formulario para cambiar la contraseña si el token de recuperación es válido.
     *
     * @param token Token de recuperación recibido por correo.
     * @return `ModelAndView` con vista `cambiar-contrasenia.jsp` o `error.jsp` si el token no es válido.
     */
    @GetMapping("/cambiar-contrasenia")
    public ModelAndView mostrarFormularioCambio(@RequestParam("token") String token) {
        ModelAndView mav;
        boolean tokenValido = usuarioService.validarTokenRecuperacion(token);

        if (!tokenValido) {
            mav = new ModelAndView("error");
        } else {
            mav = new ModelAndView("cambiar-contrasenia");
            mav.addObject("token", token);
        }

        return mav;
    }

    /**
     * Extrae el token de sesión JWT de las cookies (clave `SESSIONID`).
     *
     * @param request Petición HTTP con cookies.
     * @return El valor del token o `null` si no existe.
     */
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
}
