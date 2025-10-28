# language: pt
Funcionalidade: Gestão de normas

  Cenario: Criar uma norma sem campo obrigatório
    Dado que eu possuo dados incompletos de uma norma
    Quando eu envio uma requisição POST de norma para "/api/normas"
    Entao o endpoint de norma deve retornar status 400
    E o corpo da resposta de norma deve conter a mensagem "A descrição da norma é obrigatória"
