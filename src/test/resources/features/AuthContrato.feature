# language: pt
Funcionalidade: Validar o contrato ao realizar um cadastro bem-sucedido de usuário
  Como consumidor da API de autenticação
  Quero cadastrar um novo usuário com sucesso
  Para garantir que o contrato JSON retornado está conforme o esperado

  Cenario: Validar contrato do cadastro bem-sucedido de usuário
    Dado que eu tenha os seguintes dados de usuário:
      | campo  | valor               |
      | nome   | João da Silva       |
      | email  | joaosilva@email.com |
      | senha  | 123456              |
      | role   | ADMIN               |
    Quando eu enviar a requisição para o endpoint "/auth/register" de cadastro de usuário
    Então o status code da resposta deve ser 201
    E que o arquivo de contrato esperado é o "Cadastro ok de usuário"
    Então a resposta da requisição deve estar em conformidade com o contrato selecionado