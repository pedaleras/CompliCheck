# features/Normas.feature
Feature: Gerenciamento de Normas

  Como um usuário administrador do CompliCheck
  Eu quero gerenciar as normas de conformidade
  Para garantir que as empresas cumpram suas obrigações ESG

  Background:
    Given que um usuário ADMIN está autenticado
    # Este step "Given que um usuário ADMIN está autenticado" deve ser implementado no AuthSteps
    # e deve gerar um token JWT válido e armazená-lo no TestContext para ser usado nas requisições.
    # Ex: TestContext.setAuthToken("Bearer <token>");
    And que existe uma empresa com ID 1 e nome "Empresa Teste"
    # Este step "And que existe uma empresa com ID 1 e nome "Empresa Teste"" deve criar
    # uma empresa ou garantir que uma empresa padrão (com ID 1) exista no banco de dados para os testes.

  Scenario: Cadastrar uma nova norma com sucesso
    When eu faço uma requisição POST para o endpoint "/api/normas" com o seguinte JSON:
      """
      {
        "descricao": "Norma ISO 14001 para Meio Ambiente",
        "categoria": "Ambiental",
        "dataLimite": "2025-12-31",
        "empresaId": 1
      }
      """
    Then o status da resposta deve ser 201
    And o corpo da resposta deve conter "id"
    And o corpo da resposta deve conter "descricao": "Norma ISO 14001 para Meio Ambiente"
    And o corpo da resposta deve conter "categoria": "Ambiental"
    And o corpo da resposta deve conter "dataLimite": "2025-12-31"
    And o corpo da resposta deve conter "empresa": {"id":1,"nome":"Empresa Teste"}
    And eu salvo o "id" da resposta como "idNormaCriada"

  Scenario: Tentar cadastrar norma com descrição em branco
    When eu faço uma requisição POST para o endpoint "/api/normas" com o seguinte JSON:
      """
      {
        "descricao": "",
        "categoria": "Ambiental",
        "dataLimite": "2025-10-26",
        "empresaId": 1
      }
      """
    Then o status da resposta deve ser 400
    And o corpo da resposta deve conter a mensagem "A descrição da norma é obrigatória"

  Scenario: Tentar cadastrar norma com data limite no passado
    When eu faço uma requisição POST para o endpoint "/api/normas" com o seguinte JSON:
      """
      {
        "descricao": "Norma com data inválida",
        "categoria": "Social",
        "dataLimite": "2023-01-01",
        "empresaId": 1
      }
      """
    Then o status da resposta deve ser 400
    And o corpo da resposta deve conter a mensagem "A data limite deve ser hoje ou no futuro"

  Scenario: Tentar cadastrar norma sem associar a uma empresa
    When eu faço uma requisição POST para o endpoint "/api/normas" com o seguinte JSON:
      """
      {
        "descricao": "Norma sem empresa",
        "categoria": "Governança",
        "dataLimite": "2025-05-20"
        // "empresaId" está ausente
      }
      """
    Then o status da resposta deve ser 400
    And o corpo da resposta deve conter a mensagem "O ID da empresa é obrigatório"

  Scenario: Tentar cadastrar norma para uma empresa inexistente
    When eu faço uma requisição POST para o endpoint "/api/normas" com o seguinte JSON:
      """
      {
        "descricao": "Norma para empresa inexistente",
        "categoria": "Ambiental",
        "dataLimite": "2025-11-15",
        "empresaId": 999999 // ID que sabemos que não existe
      }
      """
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Empresa não encontrada" # Ou a mensagem de erro específica do seu GlobalExceptionHandler

  Scenario: Listar todas as normas (assumindo que já existe pelo menos uma)
    Given que uma norma foi cadastrada com descrição "Norma para Listagem" e empresaId 1
    # Este step "Given que uma norma foi cadastrada..." deve ser implementado no NormasSteps
    # para pré-condicionar o banco com uma norma antes da busca.
    When eu faço uma requisição GET para o endpoint "/api/normas"
    Then o status da resposta deve ser 200
    And o corpo da resposta deve ser uma lista
    And a lista deve conter um item com "descricao": "Norma para Listagem"

  Scenario: Buscar norma existente por ID
    Given que uma norma foi cadastrada com ID 200 e descrição "Norma Teste para Busca" e empresaId 1
    When eu faço uma requisição GET para o endpoint "/api/normas/200"
    Then o status da resposta deve ser 200
    And o corpo da resposta deve conter "id": 200
    And o corpo da resposta deve conter "descricao": "Norma Teste para Busca"

  Scenario: Tentar buscar norma inexistente por ID
    When eu faço uma requisição GET para o endpoint "/api/normas/999999"
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Norma não encontrada"

  Scenario: Atualizar uma norma existente com sucesso
    Given que uma norma foi cadastrada com ID 300 e descrição "Norma Original" e categoria "Antiga" e dataLimite "2024-10-26" e empresaId 1
    When eu faço uma requisição PUT para o endpoint "/api/normas" com o seguinte JSON:
      """
      {
        "id": 300,
        "descricao": "Norma Atualizada",
        "categoria": "Nova Categoria",
        "dataLimite": "2026-06-30",
        "empresaId": 1
      }
      """
    Then o status da resposta deve ser 200
    And o corpo da resposta deve conter "id": 300
    And o corpo da resposta deve conter "descricao": "Norma Atualizada"
    And o corpo da resposta deve conter "categoria": "Nova Categoria"
    And o corpo da resposta deve conter "dataLimite": "2026-06-30"

  Scenario: Tentar atualizar norma inexistente
    When eu faço uma requisição PUT para o endpoint "/api/normas" com o seguinte JSON:
      """
      {
        "id": 999999,
        "descricao": "Tentativa de Atualização",
        "categoria": "Qualidade",
        "dataLimite": "2025-01-01",
        "empresaId": 1
      }
      """
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Norma não encontrada"

  Scenario: Deletar uma norma existente com sucesso
    Given que uma norma foi cadastrada com ID 400 e descrição "Norma para Deletar" e empresaId 1
    When eu faço uma requisição DELETE para o endpoint "/api/normas/400"
    Then o status da resposta deve ser 204

  Scenario: Tentar deletar norma inexistente
    When eu faço uma requisição DELETE para o endpoint "/api/normas/999999"
    Then o status da resposta deve ser 404
    And o corpo da resposta deve conter a mensagem "Norma não encontrada"