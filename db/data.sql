-- =============================================================================
-- SICA - Datos de prueba
-- Contraseña de los 4 usuarios: Acme2024!  (hash BCrypt, coste 10)
-- =============================================================================

USE sica_db;

-- ---------- Roles ----------
INSERT INTO roles (id, nombre_rol) VALUES
    (1, 'Superusuario'),
    (2, 'Supervisor de Seguridad'),
    (3, 'Guarda de Seguridad'),
    (4, 'Funcionario de Empresa');

-- ---------- Permisos granulares (definidos en BD, NO en código) ----------
INSERT INTO permisos (id, nombre_permiso, descripcion) VALUES
    (1,  'ver_usuarios',              'Consultar el listado de usuarios'),
    (2,  'crear_usuario',             'Crear usuarios del sistema'),
    (3,  'editar_usuario',            'Editar datos y rol de un usuario'),
    (4,  'eliminar_usuario',          'Eliminar un usuario'),
    (5,  'gestionar_roles_permisos',  'Asignar permisos a los roles'),
    (6,  'ver_personas',              'Consultar personas registradas'),
    (7,  'crear_persona',             'Registrar personas e invitados'),
    (8,  'editar_persona',            'Editar datos de una persona'),
    (9,  'eliminar_persona',          'Eliminar una persona'),
    (10, 'bloquear_persona',          'Activar o bloquear el acceso de una persona'),
    (11, 'ver_empresas',              'Consultar empresas del complejo'),
    (12, 'crear_empresa',             'Registrar empresas'),
    (13, 'editar_empresa',            'Editar empresas'),
    (14, 'eliminar_empresa',          'Eliminar empresas'),
    (15, 'ver_control_acceso',        'Ver la bandeja y el estado de las visitas'),
    (16, 'registrar_visita',          'Registrar una visita (cualquier flujo)'),
    (17, 'aprobar_visita',            'Aprobar visitas pendientes'),
    (18, 'rechazar_visita',           'Rechazar visitas pendientes'),
    (19, 'check_in',                  'Registrar el ingreso de una persona'),
    (20, 'check_out',                 'Registrar la salida de una persona'),
    (21, 'registrar_incidente',       'Registrar incidentes de seguridad'),
    (22, 'ver_incidentes',            'Consultar incidentes registrados'),
    (23, 'ver_reportes',              'Consultar reportes y who-is-in'),
    (24, 'generar_reporte',           'Generar reportes de visitas'),
    (25, 'consultar_bitacora',        'Consultar la bitácora de auditoría');

-- ---------- Asignación de permisos por rol (RBAC dinámico) ----------
-- Superusuario: todos los permisos.
INSERT INTO rol_permisos (rol_id, permiso_id)
    SELECT 1, id FROM permisos;

-- Supervisor de Seguridad: control de acceso, incidentes, reportes y auditoría.
INSERT INTO rol_permisos (rol_id, permiso_id) VALUES
    (2, 6), (2, 10), (2, 11), (2, 15), (2, 16), (2, 17), (2, 18),
    (2, 19), (2, 20), (2, 21), (2, 22), (2, 23), (2, 24), (2, 25);

-- Guarda de Seguridad: registro de visitas, check-in/out e incidentes.
INSERT INTO rol_permisos (rol_id, permiso_id) VALUES
    (3, 6), (3, 11), (3, 15), (3, 16), (3, 19), (3, 20), (3, 21), (3, 22), (3, 23);

-- Funcionario de Empresa: pre-registro de invitados y aprobaciones.
INSERT INTO rol_permisos (rol_id, permiso_id) VALUES
    (4, 6), (4, 7), (4, 8), (4, 11), (4, 15), (4, 16), (4, 17), (4, 18), (4, 23);

-- ---------- Usuarios de prueba (password plana: Acme2024!) ----------
INSERT INTO usuarios (id, nombre, email, password, rol_id, esta_activo) VALUES
    (1, 'Ana Superusuario',   'superusuario@zonaacme.com', '$2a$10$KotqS5T.t3xiqGltOxMfzublYu3nJdonTQ.9wUxldYHXuJp7rZ9pS', 1, TRUE),
    (2, 'Carlos Supervisor',  'supervisor@zonaacme.com',   '$2a$10$k8/6w/LbEK/nGEFJLg9hyOh9b3NxEDgRWrb02Bh1IvH/80ovv4YZu', 2, TRUE),
    (3, 'Diana Guarda',       'guarda@zonaacme.com',       '$2a$10$46Xgx7whSEmzIRq00kTTe.D0ohfvlgyZJD9h.kdwEbInXB18l2Z9e', 3, TRUE),
    (4, 'Elena Funcionaria',  'funcionario@globex.com',    '$2a$10$2K3peoe4IYokuJciIRZd6OFYBMAtO3Px8s22ac93difEtY8bTp4Ga', 4, TRUE);

-- ---------- Lookups de estados ----------
INSERT INTO persona_estados_acceso (id, nombre_estado) VALUES
    (1, 'Activo'),
    (2, 'Con Prohibicion de Ingreso');

INSERT INTO visita_estados (id, nombre_estado) VALUES
    (1, 'Dentro'),
    (2, 'Fuera'),
    (3, 'Pendiente de Aprobacion'),
    (4, 'Aprobado'),
    (5, 'Rechazado'),
    (6, 'Expirado'),
    (7, 'Pendiente de Aprobacion por Olvido'),
    (8, 'Cerrada por Sistema (Salida Olvidada)');

-- ---------- Empresas ----------
INSERT INTO empresas (id, nombre, contacto_principal) VALUES
    (1, 'Acme Corporation',   'recepcion@acme.com'),
    (2, 'Globex S.A.',        'elena@globex.com'),
    (3, 'Initech',            'contacto@initech.com'),
    (4, 'Umbrella Logistics', 'operaciones@umbrella.com');

-- ---------- Personas ----------
INSERT INTO personas (id, nombre, documento_identidad, empresa_id, tipo_persona, estado_acceso_id, url_foto) VALUES
    (1, 'Luis Ramírez',    '10000001', 1, 'Trabajador', 1, 'https://i.pravatar.cc/150?img=11'),
    (2, 'Mario Invitado',  '20000002', 2, 'Invitado',   1, 'https://i.pravatar.cc/150?img=12'),
    (3, 'Sara Trabajadora','10000003', 2, 'Trabajador', 1, 'https://i.pravatar.cc/150?img=13'),
    (4, 'Pedro Bloqueado', '10000004', 3, 'Trabajador', 2, 'https://i.pravatar.cc/150?img=14'),
    (5, 'Laura Invitada',  '20000005', 1, 'Invitado',   1, 'https://i.pravatar.cc/150?img=15'),
    (6, 'Javier Invitado', '20000006', 4, 'Invitado',   1, 'https://i.pravatar.cc/150?img=16'),
    (7, 'Carlos Trabajador','10000007',1, 'Trabajador', 1, 'https://i.pravatar.cc/150?img=17');

-- ---------- Visitas de ejemplo (una por cada flujo) ----------
INSERT INTO visitas (id, persona_id, fecha_entrada, fecha_salida, estado_visita_id, vehiculo_placa, visita_aprobada_por) VALUES
    -- Flujo 1: invitado pre-registrado, ya aprobado, listo para check-in.
    (1, 2, NULL, NULL, 4, NULL, 4),
    -- Flujo 4: persona "Dentro" desde hace 2 horas => permite probar salida olvidada.
    (2, 3, NOW() - INTERVAL 2 HOUR, NULL, 1, 'ABC-1234', NULL),
    -- Flujo 2: invitado no anunciado => bandeja de aprobaciones en tiempo real.
    (3, 5, NULL, NULL, 3, NULL, NULL),
    -- Flujo 3: carnet olvidado => bandeja de aprobaciones en tiempo real.
    (4, 7, NULL, NULL, 7, NULL, NULL),
    -- Historial: visita finalizada correctamente.
    (5, 1, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY + INTERVAL 8 HOUR, 2, NULL, NULL),
    -- Historial: visita rechazada.
    (6, 6, NULL, NULL, 5, NULL, 4);

-- ---------- Incidentes ----------
INSERT INTO incidentes (id, visita_id, reportado_por_id, fecha, descripcion) VALUES
    (1, 5, 2, NOW() - INTERVAL 1 DAY + INTERVAL 9 HOUR,
     'Intento de ingreso con placa no registrada en el portón B. Se regularizó en recepción.');

-- ---------- Bitácora de auditoría (ejemplos) ----------
INSERT INTO bitacora_auditoria (usuario_id, accion_realizada, tabla_afectada, registro_id_afectado, detalles) VALUES
    (1, 'LOGIN_EXITOSO',   'usuarios', 1, 'Inicio de sesión de la cuenta Superusuario'),
    (4, 'CREACION_PERSONA','personas', 2, 'Pre-registro del invitado Mario Invitado (documento 20000002)'),
    (3, 'CHECK_IN',        'visitas',  2, 'Check-in de Sara Trabajadora (documento 10000003)');
