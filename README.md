# Projeto Sistema Kanban (Only Skeleton)

![Screenshot](resource/images/screenshot.jpg)

## 📌 Sobre o Projeto
O **Kanban Board** é o esqueleto de uma aplicação de gerenciamento de tarefas que implementa um **board customizável** para acompanhar o fluxo de trabalho, inspirado no método **Kanban**.  
O sistema foi desenvolvido em **Java 21** com **JavaFX**, utilizando **MySQL** para persistência dos dados.

## 🚀 Funcionalidades
- Criar, selecionar e excluir boards.
- Cada board possui **colunas personalizadas** (Inicial, Pendentes, Finalizadas e Canceladas).
- Movimentação de **cards entre colunas** seguindo regras pré-definidas.
- Bloqueio e desbloqueio de cards com justificativas.
- Cancelamento de cards em qualquer estágio.
- Geração de relatórios com:
  - Tempo de conclusão das tarefas.
  - Histórico de movimentações.
  - Bloqueios (motivo, duração e desbloqueios).

## 🛠️ Tecnologias Utilizadas
- **Java 21**
- **JavaFX**
- **Maven**
- **MySQL**
- **JDBC**

## 📂 Estrutura do Projeto
```
javafx-kanban-board/
 ├── src/main/java/com/example/kanban/
 │    ├── dao/         # Interfaces de acesso a dados
 │    ├── dao/jdbc/    # Implementações JDBC
 │    ├── model/       # Entidades do sistema
 │    ├── services/    # serviços do sistema
 │    ├── ui/          # Telas em JavaFX
 │    ├── util/        # Utilitários do sistema
 │    └── MainApp.java # Classe principal
 ├── data/board.sql    # Script do banco de dados
 ├── └src/main/resource/
 │    ├── css/         # Folhas de estilos
 │    ├── icons/       # Ícones
 │    ├── images/      # Imagens
 └── pom.xml           # Configuração Maven
```

## ⚙️ Instalação e Execução
1. Clone o repositório:
   ```bash
   git clone https://github.com/robsonosbor/javafx-kanban-board.git
   cd javafx-kanban-board
   ```
2. Crie o banco de dados MySQL:
   ```bash
   mysql -u root -p < data/board.sql
   ```
3. Compile e execute com Maven:
   ```bash
   mvn clean install
   mvn javafx:run
   ```

## 📋 Requisitos do Sistema
- Java 21+
- Maven 3+
- MySQL 8+

## 👤 Autor
Desenvolvido por **Robson**  
🔗 [Meu GitHub](https://robsonosbor.github.io/robsonosbor/)

## 📜 Licença
Este projeto está sob a licença **MIT**.  
Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
