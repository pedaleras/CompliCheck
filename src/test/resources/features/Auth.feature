# language: pt
  Funcionalidade: Autenticação de usuários

  Cenario: Registrar um novo usuário com sucesso
    Dado que eu possuo dados válidos para registro
    Quando eu envio uma requisição POST de usuário para "/auth/register"
    Entao o endpoint de autenticação deve retornar status 201
    E o corpo da resposta do endpoint de autenticação deve conter o campo "nome"

  Cenario: Tentativa de login com credenciais inválidas
    Dado que eu possuo credenciais inválidas
    Quando eu envio uma requisição POST de usuário para "/auth/login"
    Entao o endpoint de autenticação deve retornar status 403