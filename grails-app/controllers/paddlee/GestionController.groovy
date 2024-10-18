package paddlee
import grails.rest.RestfulController
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import io.jsonwebtoken.JwtException

@Transactional
class GestionController extends RestfulController<Usuario> {
    static responseFormats = ['json', 'xml']
    JwtService jwtService  // Inyectar el servicio JWT
    
GestionController() {
        super(Usuario)
    }

// Acción para iniciar sesión y generar el token JWT
   def login() {
    def alias = request.JSON.alias
    def contrasena = request.JSON.contrasena

        // Buscar al usuario por alias y verificar la contraseña
        def usuario = Usuario.findByAlias(alias)
        if (usuario && usuario.contrasena == contrasena) {
            String token = jwtService.generateToken(alias)
            render([token: token, id: usuario.id, idTipoUsuario: usuario.idTipoUsuario] as JSON)
        } else {
            render(status: 401, text: 'Credenciales inválidas')
        }
    }


    // Acción para crear (guardar) un nuevo usuario
    def save() {
        def usuario = new Usuario(request.JSON) // Captura los datos del request en formato JSON
        log.debug("Incoming request data: ${request.JSON}")

        if (usuario.save(flush: true)) {
            // Si el usuario se guarda correctamente
            render([message: "Usuario creado con éxito", usuarioId: usuario.id] as JSON)
        } else {
            // Si hay errores al guardar el usuario
            render(status: 400, [message: "Error al crear el usuario", errors: usuario.errors] as JSON)
        }
    }

    def usuario(Long id) {
        if (!id) {
            render(status: 400, text: 'Missing user ID')
            return
        }

        // Fetch the user by ID
        def usuario = Usuario.get(id)
        if (!usuario) {
            render(status: 404, text: 'User not found')
            return
        }

        // Respond with the user data in JSON or XML (depending on request)
        respond usuario
    }

    // Action to update an existing user
    def update (Long id) {
        if (!id) {
            render(status: 400, text: 'Missing user ID')
            return
        }

        // Fetch the user by ID
        def usuario = Usuario.get(id)
        if (!usuario) {
            render(status: 404, text: 'User not found')
            return
        }

        // Bind request parameters to the user object
        usuario.properties = request.JSON

        try {
            // Validate and save the updated user
            if (usuario.save(flush: true)) {
                respond usuario, [status: 200]
            } else {
                // If validation fails, return a 422 with error details
                respond usuario.errors, status: 422
            }
        } catch (ValidationException e) {
            render(status: 422, text: 'Validation failed: ' + e.message)
        }
    }

// Method to delete a user by ID (DELETE)
    def delete(Long id) {
        if (!id) {
            render(status: 400, text: 'Missing user ID')
            return
        }

        def usuario = Usuario.get(id)
        if (!usuario) {
            render(status: 404, text: 'User not found')
            return
        }

        try {
            usuario.delete(flush: true)
            render(status: 204, text: 'successful deletion') // No Content, successful deletion
} catch (Exception e) {
            render(status: 500, text: 'Failed to delete user: ' + e.message)
        }
    }

// Action to return mock horarios
    def getHorarios() {
        def mockHorarios = [
            [horario_id: 1, fecha: "2024-10-11", hora: "10:00"],
            [horario_id: 2, fecha: "2024-10-13", hora: "12:00"],
            [horario_id: 3, fecha: "2024-10-15", hora: "14:00"],
            [horario_id: 4, fecha: "2024-10-17", hora: "16:00"],
            [horario_id: 5, fecha: "2024-10-19", hora: "18:00"],
            [horario_id: 6, fecha: "2024-10-21", hora: "20:00"],
            [horario_id: 7, fecha: "2024-10-23", hora: "22:00"],
            [horario_id: 8, fecha: "2024-10-25", hora: "10:00"]
        ]
        render mockHorarios as JSON
    }

    // Action to return mock canchas
    def getCanchas() {
        def mockCanchas = [
            [cancha_id: 1, nombre: "Cancha A", ubicacion: "Location A"],
            [cancha_id: 2, nombre: "Cancha B", ubicacion: "Location B"],
            [cancha_id: 3, nombre: "Cancha C", ubicacion: "Location C"]
        ]
        render mockCanchas as JSON
    }

    // Validate token and user role (idTipoUsuario)
    def validateRoleAndToken() {
        def token = request.getHeader("Authorization")?.replace("Bearer ", "")
        //def idTipoUsuario = request.JSON.idTipoUsuario
        Integer idTipoUsuario = (request.JSON.idTipoUsuario?.trim()) ? request.JSON.idTipoUsuario.toInteger() : null
        println "tipo de usuario recibido: " + idTipoUsuario
        
        if (!token) {
            render([message: 'Token es requerido', isValid: false] as JSON, status: 401)
            return
        }
        
        if (!idTipoUsuario) {
            render([message: 'idTipoUsuario es requerido', isValid: false] as JSON, status: 400)
            return
        }

        try {

            // def jwtService = grailsApplication.mainContext.getBean('jwtService')
            //     jwtService.getClass().methods.each { method ->
            //         println method.name + " " + method.parameterTypes.collect { it.simpleName }.join(', ')
            //     }
            def isValid = jwtService.validateToken(token) // Call without the secret
            println "es valido?:" + isValid
            def alias = jwtService.getUsernameFromToken(token)
            println "alias recuperado:" + alias
            def usuario = Usuario.findByAlias(alias)
            println "contrasenia encontrada: " + usuario.contrasena
            //println "idTipoUsuario de usuario:" + usuario.idTipoUsuario
            println "idTipoUsuario de usuario: ${usuario?.idTipoUsuario}"
            
            //print "tipo valido :"  (usuario.idTipoUsuario != idTipoUsuario)
            if (!usuario) {
                render([message: 'Usuario no encontrado', isValid: false] as JSON, status: 404)
                return
            }

            //print "idTipoUsuario de usuario:" + usuario.idTipoUsuario
            if (usuario.idTipoUsuario != idTipoUsuario) {
                render([message: 'Role no valido', isValid: false] as JSON, status: 403)
               return
            }

            render([message: 'Token y role Valido', isValid: true] as JSON, status: 200)

        } catch (JwtException e) {
            log.error("JWT validation failed: ${e.message}") // Log specific error message
            render([message: 'Token no valido o el mismo ha expirado: ' + e.message, isValid: false] as JSON, status: 401)
        } catch (Exception e) {
            log.error("Unexpected error: ${e.message}") // Log unexpected errors
            render([message: 'Error inesperado: ' + e.message, isValid: false] as JSON, status: 500)
        }
    }
}