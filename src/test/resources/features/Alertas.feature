@Alertas
Feature: Gerenciamento de Alertas
  Como administrador do sistema
  Quero gerenciar alertas de conformidade
  Para garantir o controle dos prazos das normas vinculadas às empresas

  # Cenários de Cadastro
  Scenario: Cadastro de alerta com sucesso
    Given que existe uma norma cadastrada com ID 1
    When eu faço uma requisição POST para o endpoint "/api/alertas" com o seguinte JSON:
      """
      {
        "status": "Pendente",
        "dataVerificacao": "2025-11-01",
        "normaId": 1
      }
      """
    Then o status da resposta deve ser 201
    And o corpo da resposta deve conter o campo "id"
    And o campo "status" deve ser "Pendente"

  Scenario: Cadastro de alerta para norma inexistente
    Given que eu quero criar um alerta para uma norma que não existe
    When eu faço uma requisição POST para o endpoint "/api/alertas" com o seguinte JSON:
      """
      {
        "status": "Pendente",
        "dataVerificacao": "2025-11-01",
        "normaId": 99
      }
      """
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Norma não encontrada"

  Scenario: Cadastro de alerta com campos obrigatórios ausentes
    Given que eu envio um JSON sem o campo "status"
    When eu faço uma requisição POST para o endpoint "/api/alertas" com o seguinte JSON:
      """
      {
        "dataVerificacao": "2025-11-01",
        "normaId": 1
      }
      """
    Then o status da resposta deve ser 400
    And o corpo da resposta deve conter a mensagem "Erro de validação"

  # Cenários de Consulta
  Scenario: Listagem de todos os alertas cadastrados
    Given que existem alertas cadastrados no sistema
    When eu faço uma requisição GET para o endpoint "/api/alertas"
    Then o status da resposta deve ser 200
    And o corpo da resposta deve conter uma lista de alertas

  Scenario: Consulta de alerta por ID com sucesso
    Given que existe um alerta cadastrado com ID 1
    When eu faço uma requisição GET para o endpoint "/api/alertas/1"
    Then o status da resposta deve ser 200
    And o corpo da resposta deve conter o alerta com ID 1

  Scenario: Consulta de alerta por ID inexistente
    Given que não existe um alerta cadastrado com ID 99
    When eu faço uma requisição GET para o endpoint "/api/alertas/99"
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Alerta não encontrado"

  # Cenários de Atualização
  Scenario: Atualização de alerta com sucesso
    Given que existe um alerta cadastrado com ID 1
    And os seguintes dados precisam ser atualizados:
      | status  | dataVerificacao |
      | Concluído | 2025-11-15      |
    When eu faço uma requisição PUT para o endpoint "/api/alertas/1" com o seguinte JSON:
      """
      {
        "status": "Concluído",
        "dataVerificacao": "2025-11-15",
        "normaId": 1
      }
      """
    Then o status da resposta deve ser 200
    And o corpo da resposta deve conter o campo "status" com valor "Concluído"

  Scenario: Atualização de alerta para norma inexistente
    Given que existe um alerta cadastrado com ID 1
    When eu faço uma requisição PUT para o endpoint "/api/alertas/1" com o seguinte JSON:
      """
      {
        "status": "Concluído",
        "dataVerificacao": "2025-11-15",
        "normaId": 99
      }
      """
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Norma não encontrada"

  Scenario: Atualização de alerta com ID inexistente
    Given que não existe um alerta cadastrado com ID 99
    When eu faço uma requisição PUT para o endpoint "/api/alertas/99" com o seguinte JSON:
      """
      {
        "status": "Concluído",
        "dataVerificacao": "2025-11-15",
        "normaId": 1
      }
      """
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Alerta não encontrado"

  Scenario: Exclusão de alerta com sucesso
    Given que existe um alerta cadastrado com ID 1
    When eu faço uma requisição DELETE para o endpoint "/api/alertas/1"
    Then o status da resposta deve ser 204

  Scenario: Exclusão de alerta com ID inexistente
    Given que não existe um alerta cadastrado com ID 99
    When eu faço uma requisição DELETE para o endpoint "/api/alertas/99"
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Alerta não encontrado"