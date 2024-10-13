/*class CorsInterceptor {

    CorsInterceptor() {
        matchAll() // Aplica a todas las rutas
    }

    boolean before() {
        // Agregar encabezados CORS
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        response.setHeader("Access-Control-Allow-Headers", "*")
        response.setHeader("Access-Control-Allow-Credentials", "true")

        // Para solicitudes OPTIONS, responder sin pasar la solicitud a otros interceptores
        if (request.method == 'OPTIONS') {
            render status: 200
            return false
        }
        return true
    }

    boolean after() { true }

    void afterView() { }
}
*/