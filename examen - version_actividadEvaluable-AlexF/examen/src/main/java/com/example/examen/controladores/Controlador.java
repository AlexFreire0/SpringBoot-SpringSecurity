package com.example.examen.controladores;

import com.example.examen.modelo.Entrada;
import com.example.examen.modelo.Funcion;
import com.example.examen.modelo.Obra;
import com.example.examen.modelo.Usuario;
import com.example.examen.servicios.ServicioObras;
import com.example.examen.servicios.ServicioFunciones;
import com.example.examen.servicios.ServicioUsuarios;
import com.example.examen.servicios.ServicioEntradas;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class Controlador {

    private final ServicioObras servicioObras;
    private final ServicioFunciones servicioFunciones;
    private final ServicioUsuarios servicioUsuarios;
    private final ServicioEntradas servicioEntradas;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // Inyección automática por constructor
    public Controlador(ServicioObras servicioObras,
            ServicioFunciones servicioFunciones,
            ServicioUsuarios servicioUsuarios,
            ServicioEntradas servicioEntradas,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {

        this.servicioObras = servicioObras;
        this.servicioFunciones = servicioFunciones;
        this.servicioUsuarios = servicioUsuarios;
        this.servicioEntradas = servicioEntradas;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public ModelAndView home(Authentication aut) {

        ModelAndView mv = new ModelAndView("index");

        // Datos de autenticación para el nav
        if (aut != null) {
            mv.addObject("usuarioAutenticado", true);
            mv.addObject("emailUsuario", aut.getName());
        } else {
            mv.addObject("usuarioAutenticado", false);
        }

        // Listado de obras para la home
        mv.addObject("listaObras", servicioObras.listarObras());

        return mv;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/denegado")
    public ModelAndView accesoDenegado() {
        return new ModelAndView("denegado");
    }

    @GetMapping("/registro")
    public ModelAndView registro() {
        return new ModelAndView("registro");
    }

    @PostMapping("/registro/guardar")
    public String guardarUsuario(Usuario usuario) {
        // En un entorno real, la contraseña debería codificarse (BCrypt, etc.)
        // Configuración por defecto para nuevos usuarios
        usuario.setPerfil("CLIENTE");
        usuario.setActivo(true);
        usuario.setPw(passwordEncoder.encode(usuario.getPw()));

        servicioUsuarios.guardarUsuario(usuario);

        return "redirect:/login?registrado=true";
    }

    @GetMapping("/error")
    public ModelAndView error() {
        // Spring redirige automáticamente a este endpoint cuando se produce
        // una excepción no controlada durante la ejecución de la aplicación
        return new ModelAndView("error");
    }

    @GetMapping("/cliente")
    public ModelAndView zonaCliente(Authentication aut) {
        ModelAndView mv = new ModelAndView("entradas");
        // Asumimos que el usuario ya inició sesión,
        // de lo contrario no podría llegar aquí.
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());
        List<Funcion> funciones = servicioFunciones.listarFuncionesPosteriores();
        for (Funcion funcion : funciones)
        { System.out.println(funcion.toString()); }
        mv.addObject("listaFunciones", funciones);
        return mv;
    }

    @GetMapping("/admin")
    public ModelAndView zonaAdmin(Authentication aut) {
        ModelAndView mv = new ModelAndView("admin");

        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());

        // Datos de funciones (ya existente)
        mv.addObject("datosFunciones",
                servicioFunciones.totalEntradasVendidasPorFuncion());

        // NUEVO: Resumen por obra
        mv.addObject("resumenObras",
                servicioFunciones.resumenPorObra());

        return mv;
    }

    // Mostrar formulario para nueva obra
    @GetMapping("/admin/obra/nueva")
    public ModelAndView nuevaObra(Authentication aut) {
        ModelAndView mv = new ModelAndView("obra-form");
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());

        Obra obra = new Obra();
        obra.setId(servicioObras.obtenerSiguienteId());

        mv.addObject("obra", obra);

        return mv;
    }

    // Mostrar formulario para editar obra existente
    @GetMapping("/admin/obra/editar")
    public ModelAndView editarObra(@RequestParam int id, Authentication aut) {
        ModelAndView mv = new ModelAndView("obra-form");
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());

        Obra obra = servicioObras.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));

        mv.addObject("obra", obra);

        return mv;
    }

    // Guardar obra (nueva o editada)
    @PostMapping("/admin/obra/guardar")
    public String guardarObra(Obra obra) {
        servicioObras.guardarObra(obra);
        return "redirect:/admin";
    }

    // Eliminar obra
    @GetMapping("/admin/obra/eliminar")
    public String eliminarObra(@RequestParam int id) {
        servicioObras.eliminarObra(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/funcion/nueva")
    public ModelAndView nuevaFuncion(Authentication aut) {
        ModelAndView mv = new ModelAndView("funcion-form");
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());

        Funcion funcion = new Funcion();
        // pre-fill ID or let DB handle? The original code had explicit ID assignment
        // for Entrada.
        // For Funcion, repo logic "obtenerSiguienteId" exists.
        funcion.setId(servicioFunciones.obtenerSiguienteId());

        mv.addObject("funcion", funcion);
        mv.addObject("listaObras", servicioObras.listarObras());

        return mv;
    }

    @GetMapping("/admin/funcion/verDetalles")
    public ModelAndView VerDetallesFuncion(@RequestParam int id, Authentication aut) {
        ModelAndView mv = new ModelAndView("verDetalles");
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());
        Optional<Entrada> entrada = servicioEntradas.buscarPorId(id);
        mv.addObject("TituloObra", entrada.get().getFuncion().getObra().getTitulo());
        mv.addObject("Cantidad", entrada.get().getCantidad());
        mv.addObject("CorreoC", entrada.get().getUsuario().getEmail());
        mv.addObject("NombreCliente", entrada.get().getUsuario().getNombre());
        mv.addObject("listaObras", servicioObras.listarObras());

        return mv;
    }

    @GetMapping("/admin/funcion/editar")
    public ModelAndView editarFuncion(@RequestParam int id, Authentication aut) {
        ModelAndView mv = new ModelAndView("funcion-form");
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());

        Funcion funcion = servicioFunciones.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Función no encontrada"));

        mv.addObject("funcion", funcion);
        mv.addObject("listaObras", servicioObras.listarObras());

        return mv;
    }

    @PostMapping("/admin/funcion/guardar")
    public String guardarFuncion(Funcion funcion) {
        servicioFunciones.guardarFuncion(funcion);
        return "redirect:/admin";
    }

    @GetMapping("/admin/funcion/eliminar")
    public String eliminarFuncion(@RequestParam int id) {
        servicioFunciones.eliminarFuncion(id);
        return "redirect:/admin";
    }

    @GetMapping("/comprar")
    public ModelAndView comprar(@RequestParam int idFuncion, Authentication aut) {

        ModelAndView mv = new ModelAndView("comprar");

        // Datos del usuario para el nav
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());
        // Buscar la función
        Funcion funcion = servicioFunciones.buscarPorId(idFuncion).orElse(null);

        if (funcion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La función solicitada no existe");
            // Esto provocará la redirección a /error
        }

        mv.addObject("funcion", funcion);

        return mv;
    }
    @GetMapping("/perfil")
    public ModelAndView perfil(Authentication aut) {

        ModelAndView mv = new ModelAndView("perfil");


        // Datos del usuario para el nav
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());

        // Buscar el user
        Optional<Usuario> usuario = servicioUsuarios.buscarPorEmail(aut.getName());
        //Datos usuario
        mv.addObject("usuarioU", usuario.get().getEmail());
        mv.addObject("perfilU", usuario.get().getPerfil());
        mv.addObject("nombreU", usuario.get().getNombre());
        if(usuario.get().isActivo()){
            mv.addObject("activoU", "Esta activo");
        } else {
            mv.addObject("activoU", "No esta activo");
        }
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario solicitada no existe");
            // Esto provocará la redirección a /error
        }

        mv.addObject("Usuario", usuario);
        List<Entrada> entradas = usuario.get().getEntradas();
        mv.addObject("entradas", entradas);

        return mv;
    }

    @GetMapping("/perfil/eliminar")
    public String eliminarEntrada(@RequestParam int id) {
        servicioEntradas.eliminarFuncion(id);
        return "redirect:/perfil";
    }

    @PostMapping("/confirmarCompra")
    public ModelAndView confirmarCompra(@RequestParam int idFuncion,
            @RequestParam int cantidad,
            Authentication aut) {

        // Recuperamos la función
        Funcion funcion = servicioFunciones.buscarPorId(idFuncion).orElse(null);

        if (funcion == null) {
            throw new RuntimeException("La función seleccionada no existe");
        }

        // Creamos la entrada
        Entrada entrada = new Entrada();
        entrada.setId(servicioEntradas.obtenerSiguienteId());
        entrada.setCantidad(cantidad);
        entrada.setFechaCompra(LocalDate.now());
        entrada.setFuncion(funcion);

        // Usuario autenticado (email es la PK)
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no válido"));

        entrada.setUsuario(usuario);

        // Guardamos la entrada
        servicioEntradas.guardarEntrada(entrada);

        // Volvemos a la home
        return new ModelAndView("redirect:/");
    }
}
