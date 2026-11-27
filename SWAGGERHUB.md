# Swagger e SwaggerHub

## Dependência e endereços locais

A documentação é gerada a partir dos controllers e DTOs pela dependência `springdoc-openapi-starter-webmvc-ui` (versão `3.2.0`, compatível com Spring Boot 4). Com a aplicação em execução, os endereços são:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Especificação OpenAPI em JSON: `http://localhost:8080/v3/api-docs`
- Especificação OpenAPI em YAML: `http://localhost:8080/v3/api-docs.yaml`

A documentação define o esquema Bearer JWT. No Swagger UI, use **Authorize** e informe o access token (sem acrescentar `Bearer `; a interface o inclui). O endpoint de prioridades e as rotas de autenticação/recuperação de senha são públicos; tarefas, categorias e operações de perfil exigem token.

## Gerar e importar no SwaggerHub

1. Configure as variáveis de ambiente e os serviços necessários à aplicação (banco de dados e credenciais OAuth, quando aplicável), conforme `src/main/resources/application.yml` e `src/main/resources/application-conn.yml`.
2. Na raiz do projeto, inicie a API com `.\mvnw.cmd spring-boot:run`. Aguarde a aplicação iniciar na porta `8080`.
3. Abra `http://localhost:8080/v3/api-docs.yaml` no navegador e salve o conteúdo como `openapi.yaml`. Alternativamente, baixe com `curl.exe -o openapi.yaml http://localhost:8080/v3/api-docs.yaml`.
4. Entre em [SwaggerHub](https://app.swaggerhub.com/) e faça login.
5. Selecione **Create New** → **API** e escolha importar uma definição existente (o nome exato do botão pode variar com a interface do SwaggerHub).
6. Envie `openapi.yaml`, confira a prévia e confirme a criação. Escolha a visibilidade e a versão conforme o acesso desejado.
7. Abra a documentação criada e revise **Servers**: a especificação gerada aponta para `http://localhost:8080`; substitua-o pela URL HTTPS pública da API antes de compartilhar ou usar **Try it out** no SwaggerHub.

O SwaggerHub recebe uma cópia da especificação; alterações futuras na API não são sincronizadas automaticamente. Gere e importe novamente o YAML ao atualizar os endpoints e publique uma nova versão no SwaggerHub. Não inclua tokens, senhas ou segredos na especificação.