-- Atualiza John Doe com endereço
INSERT INTO tb_address (id, user_id, street, number, complement, neighborhood, city, state, zip_code, main, created_at)
VALUES (
    gen_random_uuid(),
    (SELECT id FROM tb_user WHERE keycloak_id = '85a925e6-6981-436f-8354-a0755b854273'),
    'Rua das Flores', '123', 'Apto 4', 'Centro', 'Belo Jardim', 'PE', '55155-100', true, NOW()
);

-- Admin
INSERT INTO tb_user (id, keycloak_id, name, email, phone, active, created_at, updated_at) VALUES
    (gen_random_uuid(), '0c082cad-0fa7-432d-a199-3d16d9c330f9', 'Admin User', 'admin@bj-food.com', '81999990010', true, NOW(), NOW());

INSERT INTO tb_address (id, user_id, street, number, complement, neighborhood, city, state, zip_code, main, created_at)
VALUES (
    gen_random_uuid(),
    (SELECT id FROM tb_user WHERE keycloak_id = '0c082cad-0fa7-432d-a199-3d16d9c330f9'),
    'Av. Pernambuco', '1000', 'Sala 10', 'Boa Vista', 'Belo Jardim', 'PE', '55155-200', true, NOW()
);

-- Restaurante
INSERT INTO tb_user (id, keycloak_id, name, email, phone, active, created_at, updated_at) VALUES
    (gen_random_uuid(), 'dbc5c200-55eb-4a62-80c3-213300dd6f4a', 'Restaurante Owner', 'restaurante@bj-food.com', '81999990020', true, NOW(), NOW());

INSERT INTO tb_address (id, user_id, street, number, complement, neighborhood, city, state, zip_code, main, created_at)
VALUES (
    gen_random_uuid(),
    (SELECT id FROM tb_user WHERE keycloak_id = 'dbc5c200-55eb-4a62-80c3-213300dd6f4a'),
    'Rua São Pedro', '500', NULL, 'São Pedro', 'Belo Jardim', 'PE', '55155-000', true, NOW()
);

-- Entregador
INSERT INTO tb_user (id, keycloak_id, name, email, phone, active, created_at, updated_at) VALUES
    (gen_random_uuid(), '7937d00a-8555-4362-ba1c-ecb4c1e433db', 'Entregador Silva', 'entregador@bj-food.com', '81999990030', true, NOW(), NOW());

INSERT INTO tb_address (id, user_id, street, number, complement, neighborhood, city, state, zip_code, main, created_at)
VALUES (
    gen_random_uuid(),
    (SELECT id FROM tb_user WHERE keycloak_id = '7937d00a-8555-4362-ba1c-ecb4c1e433db'),
    'Rua 10', '200', NULL, 'Vila da Barragem', 'Belo Jardim', 'PE', '55155-000', true, NOW()
);

-- Cliente 2
INSERT INTO tb_user (id, keycloak_id, name, email, phone, active, created_at, updated_at) VALUES
    (gen_random_uuid(), '5b133014-57ca-47c2-92e6-1bd3fdd06eb8', 'Maria Silva', 'maria.silva@email.com', '81999990040', true, NOW(), NOW());

INSERT INTO tb_address (id, user_id, street, number, complement, neighborhood, city, state, zip_code, main, created_at)
VALUES (
    gen_random_uuid(),
    (SELECT id FROM tb_user WHERE keycloak_id = '5b133014-57ca-47c2-92e6-1bd3fdd06eb8'),
    'Rua Major João', '800', 'Casa 2', 'Cohab 1', 'Belo Jardim', 'PE', '55155-001', true, NOW()
);