package paddlee

class Usuario {

    int idTipoUsuario
    String nombre
    String apellido
    String alias
    String email
    String contrasena
    String genero
    String direccion
    String tipoDeJuego
    String fotoPerfil
    String telefono
    String edad
    String nivel
    String recontrasena
    String remail

    
    static constraints = {
        idTipoUsuario(blank: false) // Solo permite valores específicos
        nombre(blank: false, maxSize: 100) // No puede estar vacío y longitud máxima de 100
        apellido(blank: false, maxSize: 100) // No puede estar vacío y longitud máxima de 100
        email(email: true, blank: false, maxSize: 100) // Debe ser un correo válido, único y no vacío
        contrasena(blank: false, password: true, maxSize: 255) // No puede estar vacía y debe cumplir con seguridad de contraseña
        recontrasena(blank: false, password: true, maxSize: 255) // No puede estar vacía y debe cumplir con seguridad de contraseña
        genero(blank: false, inList: ["Masculino", "Femenino", "Otro"]) // Solo permite valores específicos
        direccion(nullable: true, maxSize: 255) // Es opcional pero si se proporciona no debe exceder el tamaño
        tipoDeJuego(nullable: true, maxSize: 50) // Es opcional, tamaño máximo 50
        telefono(nullable: true, matches: "\\d{10}") // Opcional, pero debe ser un número de 10 dígitos
        nivel(blank: false, inList: ["intermedio", "principiante", "avanzado"]) // Solo permite valores específicos
        alias blank: false, unique: true, validator: { val, obj ->
    // Si el alias no ha cambiado, no debe validar la unicidad
    if (obj?.id && Usuario.get(obj.id)?.alias == val) {
        return true // Alias no ha cambiado, no validar unicidad
    }
    
    // Si el alias ya existe en otro registro, devuelve un error
    if (Usuario.findByAlias(val)) {
        return ['alias.existe']
    }

    return true
}
    
}
}