-- =============================================================================
-- SICA - Esquema de base de datos
-- Réplica exacta del modelo de datos del documento de requerimientos.
-- MySQL 8.0 - Se ejecuta automáticamente al crear el volumen de Docker.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS sica_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE sica_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS bitacora_auditoria;
DROP TABLE IF EXISTS incidentes;
DROP TABLE IF EXISTS visitas;
DROP TABLE IF EXISTS personas;
DROP TABLE IF EXISTS empresas;
DROP TABLE IF EXISTS visita_estados;
DROP TABLE IF EXISTS persona_estados_acceso;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS rol_permisos;
DROP TABLE IF EXISTS permisos;
DROP TABLE IF EXISTS roles;

SET FOREIGN_KEY_CHECKS = 1;

-- =========== TABLAS DE AUTENTICACIÓN Y AUTORIZACIÓN (RBAC) ===========

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE permisos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_permiso VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rol_permisos (
    rol_id INT,
    permiso_id INT,
    PRIMARY KEY (rol_id, permiso_id),
    FOREIGN KEY (rol_id) REFERENCES roles(id),
    FOREIGN KEY (permiso_id) REFERENCES permisos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol_id INT,
    esta_activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (rol_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========== TABLAS DE CONSULTA (LOOKUP TABLES) PARA ESTADOS ===========

CREATE TABLE persona_estados_acceso (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE visita_estados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========== TABLAS OPERACIONALES DEL NEGOCIO ===========

CREATE TABLE empresas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    contacto_principal VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE personas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(20) UNIQUE NOT NULL,
    empresa_id INT,
    tipo_persona ENUM('Trabajador', 'Invitado') NOT NULL,
    estado_acceso_id INT,
    url_foto VARCHAR(255),
    FOREIGN KEY (empresa_id) REFERENCES empresas(id),
    FOREIGN KEY (estado_acceso_id) REFERENCES persona_estados_acceso(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE visitas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    persona_id INT,
    fecha_entrada DATETIME,
    fecha_salida DATETIME,
    estado_visita_id INT,
    vehiculo_placa VARCHAR(10),
    visita_aprobada_por INT,
    FOREIGN KEY (persona_id) REFERENCES personas(id),
    FOREIGN KEY (estado_visita_id) REFERENCES visita_estados(id),
    FOREIGN KEY (visita_aprobada_por) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE incidentes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    visita_id INT,
    reportado_por_id INT,
    fecha DATETIME NOT NULL,
    descripcion TEXT NOT NULL,
    FOREIGN KEY (visita_id) REFERENCES visitas(id),
    FOREIGN KEY (reportado_por_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========== TABLA DE AUDITORÍA ===========

CREATE TABLE bitacora_auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accion_realizada VARCHAR(255) NOT NULL,
    tabla_afectada VARCHAR(100),
    registro_id_afectado INT,
    detalles TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices de apoyo (no alteran columnas ni relaciones del modelo del PDF).
CREATE INDEX idx_visitas_persona ON visitas(persona_id);
CREATE INDEX idx_visitas_estado ON visitas(estado_visita_id);
CREATE INDEX idx_incidentes_visita ON incidentes(visita_id);
CREATE INDEX idx_bitacora_fecha ON bitacora_auditoria(fecha_hora);
