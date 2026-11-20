# HelpDesk Mobile

Aplicativo Android em Java para abertura, acompanhamento e atendimento de chamados de manutenção, suporte tecnico e problemas estruturais.

Este projeto foi montado para Android Studio a partir da documentacao `HelpDesk_Mobile_Documentacao.pdf`. A primeira versao foca em funcionamento local, organizacao do codigo e clareza para estudo: login, cadastro, perfis, abertura de chamados, listagem, detalhes, atualizacao tecnica, historico basico, filtros e logout.

## Tecnologias

- Android Studio
- Java
- XML para interface
- SQLite local
- SharedPreferences para sessao
- RecyclerView para listas
- CardView para os cards dos chamados

## Como abrir no Android Studio

1. Abra o Android Studio.
2. Selecione `Open`.
3. Escolha a pasta do repositório que contém `settings.gradle`.
4. Aguarde o Gradle sincronizar.
5. Execute o app em um emulador ou celular Android.

O projeto usa o Android Gradle Plugin 9.1.0 e o SDK Android 36.1. O Android Studio configura o caminho do SDK em `local.properties` para cada máquina. Para compilar pelo terminal no Windows, execute `gradlew.bat :app:assembleDebug` com o JDK do Android Studio.

## Contas iniciais para teste

| Perfil | E-mail | Senha | Tipo |
| --- | --- | --- | --- |
| Usuario comum | `joao@email.com` | `123456` | `usuario` |
| Tecnico | `tecnico@email.com` | `123456` | `tecnico` |
| Administrador | `admin@email.com` | `123456` | `admin` |

Para criar novos usuarios do tipo `tecnico` ou `admin`, use o codigo de autorizacao:

```text
HELPDESK2026
```

Esse codigo foi incluido porque a documentacao solicita que o cadastro de tecnico e administrador nao fique aberto para qualquer pessoa.

## Funcionalidades implementadas

- Tela de boas-vindas com entrada e cadastro.
- Login com validacao de e-mail, senha obrigatoria e credenciais salvas no SQLite.
- Cadastro de usuario com nome, e-mail, senha, confirmacao, tipo e codigo de autorizacao para perfis tecnicos.
- Separacao de perfil comum, tecnico e administrador.
- Home do usuario comum com resumo de chamados por status.
- Cadastro de chamado com titulo, descricao, categoria, prioridade, local e anexo opcional.
- Criacao automatica do chamado com status `Aberto`.
- Listagem dos chamados do usuario comum.
- Listagem geral para tecnico e administrador.
- Filtros por status, categoria, prioridade e pesquisa por titulo ou solicitante.
- Tela de detalhes com dados completos do chamado.
- Cancelamento pelo usuario comum apenas quando o chamado ainda esta `Aberto`.
- Atualizacao de status e resposta tecnica por tecnico ou administrador.
- Registro basico no historico sempre que um chamado e criado, cancelado ou atendido.
- Logout com limpeza da sessao local.

## Regras de negocio principais

- Todo chamado novo inicia com status `Aberto`.
- Usuario comum visualiza apenas os chamados que criou.
- Tecnico e administrador visualizam todos os chamados.
- Apenas tecnico ou administrador atualizam status e resposta tecnica.
- Usuario comum so pode cancelar chamado com status `Aberto`.
- Chamado nao pode ser criado sem titulo, descricao, categoria, prioridade e local.
- Prioridades permitidas: `Baixa`, `Media` e `Alta`.
- Categorias permitidas: `Informatica`, `Eletrica`, `Hidraulica`, `Estrutura`, `Limpeza`, `Seguranca` e `Outro`.

## Estrutura do projeto

```text
HelpDesk/
|-- README.md
|-- settings.gradle
|-- build.gradle
|-- gradle.properties
|-- gradle/wrapper/
|-- app/
    |-- build.gradle
    |-- src/main/
        |-- AndroidManifest.xml
        |-- java/com/example/helpdesk/
        |   |-- MainActivity.java
        |   |-- adapters/ChamadoAdapter.java
        |   |-- dao/UsuarioDAO.java
        |   |-- dao/ChamadoDAO.java
        |   |-- dao/HistoricoChamadoDAO.java
        |   |-- database/DatabaseHelper.java
        |   |-- models/Usuario.java
        |   |-- models/Chamado.java
        |   |-- models/HistoricoChamado.java
        |   |-- utils/SessaoUsuario.java
        |   |-- utils/Validador.java
        |-- res/layout/
        |   |-- activity_main.xml
        |   |-- telas_formularios.xml
        |   |-- lista_chamados.xml
        |   |-- filtros_chamados.xml
        |   |-- item_chamado.xml
        |-- res/values/
            |-- colors.xml
            |-- strings.xml
            |-- styles.xml
```

## Banco de dados SQLite

O banco local chama `helpdesk_mobile.db` e e criado pela classe `DatabaseHelper`.

### Tabela `usuarios`

```sql
CREATE TABLE usuarios (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    senha TEXT NOT NULL,
    tipo_usuario TEXT NOT NULL,
    data_cadastro TEXT NOT NULL
);
```

### Tabela `chamados`

```sql
CREATE TABLE chamados (
    id_chamado INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    titulo TEXT NOT NULL,
    descricao TEXT NOT NULL,
    categoria TEXT NOT NULL,
    prioridade TEXT NOT NULL,
    local TEXT NOT NULL,
    status TEXT NOT NULL,
    resposta_tecnico TEXT,
    id_tecnico INTEGER,
    data_abertura TEXT NOT NULL,
    data_atualizacao TEXT,
    anexo_imagem TEXT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_tecnico) REFERENCES usuarios(id_usuario)
);
```

### Tabela `historico_chamados`

```sql
CREATE TABLE historico_chamados (
    id_historico INTEGER PRIMARY KEY AUTOINCREMENT,
    id_chamado INTEGER NOT NULL,
    id_usuario_responsavel INTEGER NOT NULL,
    acao TEXT NOT NULL,
    observacao TEXT,
    status_anterior TEXT,
    status_novo TEXT,
    data_acao TEXT NOT NULL,
    FOREIGN KEY (id_chamado) REFERENCES chamados(id_chamado),
    FOREIGN KEY (id_usuario_responsavel) REFERENCES usuarios(id_usuario)
);
```

## Observacoes importantes

- As senhas estao salvas em texto puro porque o objetivo aqui e academico e local. Em um aplicativo real, use hash seguro, autenticacao adequada e regras de permissao no backend.
- O campo de anexo salva apenas um texto/caminho opcional nesta versao. A captura real de imagem pode ser uma melhoria futura.
- O app usa uma unica `MainActivity` para facilitar a leitura, mas as regras de dados ja estao separadas em DAO, modelos, utilitarios e helper do banco.
