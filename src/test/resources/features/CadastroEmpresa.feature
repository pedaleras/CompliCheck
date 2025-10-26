@CadastroEmpresa
Feature: Cadastro de empresa
  Como um administrador do sistema
  Quero cadastrar, atualizar, buscar e excluir empresas
  Para gerenciar os dados corporativos no sistema

  # Cenários de Cadastro
  Scenario: Cadastro de empresa com sucesso
    Given que eu desejo cadastrar uma nova empresa com os seguintes dados:
      | nome          | cnpj               | setor      |
      | Empresa Teste | 12.345.678/0001-23 | Tecnologia |
    When eu faço uma requisição POST para o endpoint "/api/empresas" com o seguinte JSON:
      """
      {
        "nome": "Empresa Teste",
        "cnpj": "12.345.678/0001-23",
        "setor": "Tecnologia"
      }
      """
    Then o status da resposta deve ser 201
    And o corpo da resposta deve conter o campo "id"
    And o campo "nome" deve ser "Empresa Teste"

  Scenario: Cadastro de empresa com CNPJ duplicado
    Given que já existe uma empresa cadastrada com CNPJ "12.345.678/0001-23"
    When eu faço uma requisição POST para o endpoint "/api/empresas" com o seguinte JSON:
      """
      {
        "nome": "Outra Empresa",
        "cnpj": "12.345.678/0001-23",
        "setor": "Educação"
      }
      """
    Then o status da resposta deve ser 400
    And o corpo da resposta deve conter a mensagem "CNPJ já cadastrado"

  Scenario: Cadastro de empresa sem campos obrigatórios
    Given que eu envio um JSON sem o campo "nome"
    When eu faço uma requisição POST para o endpoint "/api/empresas" com o seguinte JSON:
      """
      {
        "cnpj": "23.456.789/0001-34",
        "setor": "Saúde"
      }
      """
    Then o status da resposta deve ser 400
    And o corpo da resposta deve conter a mensagem "Erro de validação"

  # Cenários de Consulta
  Scenario: Consulta de todas as empresas cadastradas
    Given que existem empresas cadastradas no sistema
    When eu faço uma requisição GET para o endpoint "/api/empresas"
    Then o status da resposta deve ser 200
    And o corpo da resposta deve conter uma lista de empresas

  Scenario: Consulta de empresa por ID com sucesso
    Given que existe uma empresa cadastrada com ID 1
    When eu faço uma requisição GET para o endpoint "/api/empresas/1"
    Then o status da resposta deve ser 200
    And o corpo da resposta deve conter a empresa com ID 1

  Scenario: Consulta de empresa por ID inexistente
    Given que não existe uma empresa cadastrada com ID 99
    When eu faço uma requisição GET para o endpoint "/api/empresas/99"
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Empresa não encontrada"

  # Cenários de Atualização
  Scenario: Atualização de empresa com sucesso
    Given que existe uma empresa cadastrada com ID 1
    And os seguintes dados precisam ser atualizados:
      | nome               | setor    |
      | Empresa Atualizada | Inovação |
    When eu faço uma requisição PUT para o endpoint "/api/empresas" com o seguinte JSON:
      """
      {
        "id": 1,
        "nome": "Empresa Atualizada",
        "cnpj": "12.345.678/0001-23",
        "setor": "Inovação"
      }
      """
    Then o status da resposta deve ser 200
    And o corpo da resposta deve conter o campo "nome" com valor "Empresa Atualizada"

  Scenario: Atualização de empresa inexistente
    Given que não existe uma empresa cadastrada com ID 99
    When eu faço uma requisição PUT para o endpoint "/api/empresas" com o seguinte JSON:
      """
      {
        "id": 99,
        "nome": "Empresa Inexistente",
        "cnpj": "23.456.789/0001-34",
        "setor": "Financeiro"
      }
      """
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Empresa não encontrada"

  # Cenários de Exclusão
  Scenario: Exclusão de empresa com sucesso
    Given que existe uma empresa cadastrada com ID 1
    When eu faço uma requisição DELETE para o endpoint "/api/empresas/1"
    Then o status da resposta deve ser 204

  Scenario: Exclusão de empresa inexistente
    Given que não existe uma empresa cadastrada com ID 99
    When eu faço uma requisição DELETE para o endpoint "/api/empresas/99"
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Empresa não encontrada"