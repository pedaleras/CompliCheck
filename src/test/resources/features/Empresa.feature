# language: pt
Funcionalidade: Gestão de empresas

  Cenario: Criar empresa com sucesso
    Dado que eu possuo dados válidos de uma empresa
    Quando eu envio uma requisição POST de empresa para "/api/empresas"
    Entao o endpoint de empresa deve retornar status 201
    E o corpo da resposta do endpoint de empresa deve conter o campo "nome"

  Cenario: Buscar lista de empresas
    Quando eu envio uma requisição GET de empresa para "/api/empresas"
    Entao o endpoint de empresa deve retornar status 200
    E o corpo da resposta deve conter uma lista de empresas
