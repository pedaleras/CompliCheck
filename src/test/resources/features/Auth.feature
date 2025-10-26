@Auth
Feature: Autenticação e Registro de Usuários
  Como um usuário do sistema
  Quero realizar login e registro na aplicação
  Para acessar recursos protegidos usando autenticação baseada em token

  # Cenários de Login
  Scenario: Login bem-sucedido
    Given que eu tenho as credenciais válidas:
      | email             | senha    |
      | usuario@teste.com | senha123 |
    When eu faço uma requisição POST para o endpoint "/auth/login" com o seguinte JSON:
      """
      {
        "email": "usuario@teste.com",
        "senha": "senha123"
      }
      """
    Then o status da resposta deve ser 200
    And o corpo da resposta deve conter uma propriedade "token"

  Scenario: Login com credenciais inválidas
    Given que eu tenho as credenciais inválidas:
      | email                     | senha       |
      | usuarioInvalido@teste.com | senhaErrada |
    When eu faço uma requisição POST para o endpoint "/auth/login" com o seguinte JSON:
      """
      {
        "email": "usuarioInvalido@teste.com",
        "senha": "senhaErrada"
      }
      """
    Then o status da resposta deve ser 401
    And o corpo da resposta deve conter a mensagem "Credenciais inválidas"

  Scenario: Tentativa de login sem campos obrigatórios
    Given que eu envio um JSON sem o campo "senha"
    When eu faço uma requisição POST para o endpoint "/auth/login" com o seguinte JSON:
      """
      {
        "email": "usuario@teste.com"
      }
      """
    Then o status da resposta deve ser 400
    And o corpo da resposta deve conter a mensagem "Erro de validação dos dados"

  # Cenário de Registro
  Scenario: Registro de novo usuário com sucesso
    Given que desejo registrar um novo usuário com os seguintes dados:
      | nome      | email          | senha    | role |
      | Usuario 1 | novo@teste.com | senha123 | USER |
    When eu faço uma requisição POST para o endpoint "/auth/register" com o seguinte JSON:
      """
      {
        "nome": "Usuario 1",
        "email": "novo@teste.com",
        "senha": "senha123",
        "role": "USER"
      }
      """
    Then o status da resposta deve ser 201
    And o corpo da resposta deve conter o campo "id"
    And o email retornado deve ser "novo@teste.com"

  Scenario: Tentativa de registrar um usuário sem email
    Given que desejo registrar um novo usuário sem informar o campo "email"
    When eu faço uma requisição POST para o endpoint "/auth/register" com o seguinte JSON:
      """
      {
        "nome": "Usuario 1",
        "senha": "senha123",
        "role": "USER"
      }
      """
    Then o status da resposta deve ser 400
    And o corpo da resposta deve conter a mensagem "Erro de validação dos dados"