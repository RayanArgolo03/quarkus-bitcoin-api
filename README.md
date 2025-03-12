
<!-- TÍTULO -->
# Bitcoin Exchange com Quarkus, Postgresql, Docker, Panache Hibernate, Insomnia, JWT, Keycloak, Flyway, Liquibase, MongoDB, Jax-RS, FaultTolerance, Mapstruct, Lombok e RestClient
<!-- SUB TÍTULO-->
##  Transações fictícias de Bitcoins

<!-- LOGO -->
<br />
<p align="center">
  <img src="https://github.com/user-attachments/assets/e4626a61-3508-4e2f-a8de-faece3c09723" width="650px" height="350px">
</p>

<!-- DESCRIÇÃO DOS ENDPOINTS-->
## Endpoints
### Autenticação e autorização
> Em construção 🚧🚧
<p align="center">
  <img src="https://github.com/user-attachments/assets/e82f8eb4-3702-4c24-9b38-0dc3ee3dbc0c" width="300px" height="350px">
</p>

### Transações de Bitcoins
> Em construção 🚧🚧
<p align="center">
  <img src="https://github.com/user-attachments/assets/0165038a-b47c-4676-8219-5b47b6daccf7" width="300px" height="350px">
</p>

### Operações de conta do cliente
> Em construção 🚧🚧
<p align="center">
  <img src="https://github.com/user-attachments/assets/ad0c3217-7f9b-44c2-8859-b5c14c4c5842" width="300px" height="350px">
</p>

### Busca de endereço com API ViaCEP
> Em construção 🚧🚧
<p align="center">
  <img src="https://github.com/user-attachments/assets/1ad4acca-6d19-4c8c-b3ae-e4100f3ec53b" width="350px" height="100px">
</p>

### Cotação de valor da Bitcoin com API BrasilBitcoin
> Em construção 🚧🚧
<p align="center">
  <img src="https://github.com/user-attachments/assets/e11f534e-eee0-4545-913c-e28a9cf90f72" width="350px" height="100px">
</p>
 
<!--  <h1 align="center">
   <img alt="Listar Produtos" title="Listar Produtos" src="./assets/quarkus-logo.png" width="600px" />
</h1>

## Listar Produtos (API)
<h1 align="center">
    <img alt="Listar Produtos" title="Listar Produtos" src="./assets/listar-produtos.png" width="600px" />
</h1>

## Cadastrar Produto (API)
<h1 align="center">
    <img alt="Cadastrar Produto" title="Cadastrar Produto" src="./assets/cadastrar-produto.png" width="600px" />
</h1>  -->

<!-- TABLE OF CONTENTS -->

## Tabela de Conteúdo
> Em construção 🚧🚧

<!-- 
- [Tabela de Conteúdo](#tabela-de-conte%C3%BAdo)
- [Sobre o Projeto](#sobre-o-projeto)
  - [Feito Com](#feito-com)
- [Começando](#come%C3%A7ando)
  - [Pré-requisitos](#pr%C3%A9-requisitos)
  - [Estrutura de Arquivos](#estrutura-de-arquivos)
  - [Instalação](#instala%C3%A7%C3%A3o)
  - [Edição](#edi%C3%A7%C3%A3o)
  - [Publicação](#publica%C3%A7%C3%A3o)
- [Contribuição](#contribui%C3%A7%C3%A3o)
- [Licença](#licen%C3%A7a)
- [Contato](#contato)  -->

<!-- ABOUT THE PROJECT -->

## Sobre o Projeto
> Em construção 🚧🚧


### Feito Com
- [JAVA](https://www.java.com/pt_BR/download/) - Java é uma linguagem de programação e plataforma computacional lançada pela primeira vez pela Sun Microsystems em 1995. Existem muitas aplicações e sites que não funcionarão, a menos que você tenha o Java instalado, e mais desses são criados todos os dias;
- [Quarkus](https://quarkus.io/) - A Red Hat lançou o Quarkus, um framework Java nativo do Kubernetes feito sob medida para o GraalVM e OpenJDK HotSpot. O Quarkus visa tornar o java uma plataforma líder em ambientes serverless e Kubernetes, oferecendo aos desenvolvedores um modelo unificado de programação reativa e imperativa;
- [Hibernate](http://hibernate.org/) - O Hibernate é um framework para o mapeamento objeto-relacional escrito na linguagem Java.
- [Hibernate Validator](https://hibernate.org/validator/) - Permite implementar validações dos dados das requisições

<!-- GETTING STARTED -->

## Iniciando projeto
> Em construção 🚧🚧


<!--  Para reproduzir o exemplo, é necessário seguir os requisitos mínimos.

### Pré-requisitos

 - Você vai precisar de uma IDE como por exemplo: IntelliJ IDEA, Eclipse, VSCode.
 - Instale a JDK 8 or 11+
 - Instale o Apache Maven 3.6+
 - Panache Entity
 - Docker (Apenas para subir o banco de dados Postgres Localmente) 
 - Escolha um cliente para conectar com o Banco de dados, exemplo: DBeaver, PGAdmin, Postico (Mac)
 - Cliente para realizar requisições REST: Postman ou o Insomnia.
 - Conta no Github (repositório de Código)


 #### Docker
 - Escolha um cliente para conectar com o Banco de dados, exemplo: DBeaver, PGAdmin, Postico (Mac)
 - Cliente para realizar requisições REST: Postman ou o Insomnia.
 - Instruções Adicionais:
 - Instalação do Docker (Documentação oficial)
 - Instalando Docker no windows: (Youtube, ESR)
 - Instalando o Docker no Linux: (Youtube: LinuxTips)
 - Instalando o Docker no Mac: (Youtube: Wellington Rogati)

### Estrutura de Arquivos

A estrutura de arquivos está da seguinte maneira:

```bash
quarkus-product
.
├── Procfile
├── README-Quarkus.md
├── README.md
├── assets
│   ├── cadastrar-produto.png
│   └── listar-produtos.png
├── mvnw
├── mvnw.cmd
├── pom.xml
├── postman
│   └── Quarkus-Products.postman_collection.json
├── quarkus-product.iml
├── src
│   ├── main
│   │   ├── docker
│   │   │   ├── Dockerfile.jvm
│   │   │   └── Dockerfile.native
│   │   ├── java
│   │   │   └── br
│   │   │       └── com
│   │   │           └── mp
│   │   │               └── product
│   │   │                   ├── api
│   │   │                   │   └── ProductResource.java
│   │   │                   ├── model
│   │   │                   │   └── Product.java
│   │   │                   └── repository
│   │   │                       └── ProductRepository.java
│   │   └── resources
│   │       ├── META-INF
│   │       │   └── resources
<<<<<<< HEAD
│   │       │       └── pageNumber.html
=======
│   │       │       └── pageIndex.html
>>>>>>> d1db1c8a784969b9ee96c6f2f611eb1a4bc7b379
│   │       └── application.properties
│   └── test
│       └── java
│           └── br
│               └── com
│                   └── mp
│                       └── product
│                           └── api
│                               ├── NativeProductResourceIT.java
│                               └── ProductResourceTest.java
├── system.properties
└── target
    ├── classes
    │   ├── META-INF
    │   │   └── resources
<<<<<<< HEAD
    │   │       └── pageNumber.html
=======
    │   │       └── pageIndex.html
>>>>>>> d1db1c8a784969b9ee96c6f2f611eb1a4bc7b379
    │   ├── application.properties
    │   └── br
    │       └── com
    │           └── mp
    │               └── product
    │                   ├── api
    │                   │   └── ProductResource.class
    │                   ├── model
    │                   │   └── Product.class
    │                   └── repository
    │                       └── ProductRepository.class
    ├── generated-sources
    │   └── annotations
    ├── maven-status
    │   └── maven-compiler-plugin
    │       └── compile
    │           └── default-compile
    │               ├── createdFiles.lst
    │               └── inputFiles.lst
    ├── quarkus
    │   └── bootstrap
    │       └── dev-app-model.dat
    └── wiring-devmode

43 directories, 28 files

```

### Criação da aplicação

1. Para criar o projeto, basta utlizar o template do Maven + Quarkus, conforme o comando abaixo:

```sh
mvn io.quarkus:quarkus-maven-plugin:1.0.1.Final:create \
     -DprojectGroupId=br.com.food \
     -DprojectArtifactId=quarkus-food \
     -DclassName="br.com.food.resource.FoodResource" \
     -Dpath="/food"
```

(Alternativo) - O Quarkus disponibiliza um site chamado `https://code.quarkus.io/`, onde é posísvel configurar o projeto de uma forma mais visual, vale a pena conferir, segue o link: https://code.quarkus.io/

---

#### Executando a Instância do Postgresql no Docker 

Para iniciar o Postgresql, basta rodar o comando abaixo (O Docker precisa estar instalado): 

```sh
docker run --name postgres-product -e  "POSTGRES_PASSWORD=postgres" -p 5432:5432 -v ~/developer/PostgreSQL:/var/lib/postgresql/data -d postgres

```

### Executando o projeto em Quarkus

Para executar um projeto em Quarkus, basta executar o comando: 
```sh
mvn compile quarkus:dev
```

<!-- CONTRIBUTING -->

<!--  ## Contribuição

Fique a vontade para contribuir com o projeto.

1. Faça um Fork do projeto
2. Crie uma Branch para sua Feature (`git checkout -b feature/newFeature`)
3. Adicione suas mudanças (`git add .`)
4. Comite suas mudanças (`git commit -m 'Nova funcionalidade para facilitar ...`)
5. Faça o Push da Branch (`git push origin feature/newFeature`)
6. Abra um Pull Request

-- English
- Make a fork;
- Create a branck with your feature: `git checkout -b my-feature`;
- Commit changes: `git commit -m 'feat: My new feature'`;
- Make a push to your branch: `git push origin my-feature`.

After merging your receipt request to done, you can delete a branch from yours. -->

## Prazo para conclusão: Código fonte já pronto, só falta finalizar o readme❗❗
