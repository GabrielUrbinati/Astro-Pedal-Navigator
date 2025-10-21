# Projeto Final: Desenvolvimento de Jogo Digital para Reabilitação com IoT e libGDX

## 🚀 Astro-Pedal Navigator: Reabilitação Gamificada

**Objetivo:** Desenvolver um **Jogo Sério** focado no processo de reabilitação física. O jogo utiliza o framework **libGDX** e integra-se com um dispositivo **IoT (Internet das Coisas)** para coletar dados de pedaladas em uma bicicleta ergométrica, transformando o exercício em uma experiência gamificada.

O jogo deve ser funcional e incentivar a melhoria do paciente/jogador, utilizando a **velocidade (RPM)** e a **consistência das pedaladas** como métricas de controle e avaliação.

---

##  Requisitos Técnicos e Funcionais

### Tecnologia e Framework

**Framework:** Implementação completa utilizando **libGDX** (Java).

**Controle com IoT (Bicicleta Estática):**
     O controle do jogo é **exclusivamente** via coleta de dados de pedaladas de um dispositivo IoT (Simulação de leitura Serial/Bluetooth de RPM).
     A movimentação e as ações do personagem/jogo são **diretamente influenciadas** pela velocidade (RPM) e consistência das pedaladas.
 **Mecânica Abstrata:** O tema do jogo é sobre navegação espacial, mas a mecânica de controle é a coleta do movimento de pedalar (RPM).

### Estrutura do Jogo

**Fases/Níveis:** O jogo deve conter **no mínimo 3 níveis ou fases distintos**, com dificuldade progressiva (Fase 1: Órbita, Fase 2: Asteroides, Fase 3: Minhoca).
**Personagem:** Inclui uma Nave Espacial (personagem principal).
**Menu:** Possui um menu principal que permite **Iniciar**, **Pausar** e **Reiniciar** o jogo.
**Funcionalidade:** O jogo deve ser operacional, demonstrando todas as mecânicas de controle IoT implementadas.

### Precisão e Avaliação (Métricas)

**Pontuação e Controle:** A **velocidade (RPM)** e a **consistência (Desvio Padrão da RPM)** são monitoradas e utilizadas como:
    Métrica de controle do personagem (Altitude da Nave).
    Métrica para carregar/ativar habilidades (Escudo de Consistência).
    Indicador de progresso na reabilitação ao longo das fases.

---

##  Detalhes Técnicos de Implementação

### Arquitetura de Integração IoT (Simulação/Real)

A comunicação entre a bicicleta ergométrica (via sensor de RPM) e o jogo (libGDX) segue o fluxo:

1.  **Sensor de RPM (IoT):** Mede a frequência de rotação por minuto (RPM) do pedal.
2.  **Módulo de Comunicação (Hardware):** Microcontrolador (e.g., ESP32) calcula o RPM e envia para o PC via **Interface Serial (USB) ou Bluetooth (BLE)**.
3.  **Interface de Leitura (Java/libGDX):** Classe conceitual `IoTSimulator` no libGDX lê continuamente os dados da porta.

#### Tratamento de Dados (Filtering)

Para garantir um controle suave, os dados brutos de RPM são filtrados:

**Amostragem:** Leitura a cada 50ms.
**Filtro de Média Móvel (Smoothing):** Média das últimas 5 leituras para suavizar picos (ruído), garantindo uma resposta de nave fluida.

### Implementação das Métricas de Avaliação

#### Métrica 1: Velocidade de Pedalada (RPM)

Governa o **Controle Vertical (Altitude)** da nave.

| Variável | Descrição |
| :--- | :--- |
| `currentRPM` | RPM lida do IoT (filtrada). |
| `targetRPM` | RPM ideal (ajustável por nível). |

**Mecânica de Controle (Movimento da Nave):**

A nave sobe se o `currentRPM` for maior que o `targetRPM` e desce se for menor.
A nave flutua estavelmente apenas quando o paciente pedala exatamente no RPM alvo (ponto ideal de reabilitação).

#### Métrica 2: Consistência (Variação de RPM)

Mede a uniformidade do exercício. É usada para carregar o **Escudo de Energia**.

**Cálculo da Consistência:**

Avaliada pelo **Desvio Padrão (DP)** das últimas leituras de RPM (N=10, cobrindo 0.5s).
Um DP baixo indica pedalada suave e consistente.

**Mecânica de Jogo (Escudo):**

**Escudo Ativo:** Se o Desvio Padrão da RPM (`DP_RPM`) for menor que um limite (`Threshold`).
**Função:** O escudo permite que a nave absorva um impacto de asteroide por fase.

### Implementação dos Níveis (Dificuldade Progressiva)

O nível de dificuldade é ajustado via variáveis do jogo, forçando o jogador a adaptar sua pedalada para o objetivo terapêutico.

| Nível | Ajuste da Pista (Gameplay) | Ajuste da Métrica (Terapêutico) |
| :--- | :--- | :--- |
| **Fase 1 (Órbita)** | Pista de navegação larga. Velocidade de rolagem lenta. | `targetRPM` = 50. Baixa exigência de consistência. |
| **Fase 2 (Asteroides)** | Pista estreita. Maior densidade de obstáculos. | `targetRPM` = 65. Threshold de Consistência reduzido (exige DP menor). |
| **Fase 3 (Minhoca)** | Pista oscilante. Tempo de fase alongado. | `targetRPM` = 80. Foco na resistência de alta RPM. |

### Avaliação e Relatório

Ao final de cada nível, o jogo coleta os seguintes dados para feedback:

* **RPM Média Total:** Força e intensidade médias.
* **Tempo de Consistência:** Porcentagem do tempo em que o escudo estava ativo (qualidade do ritmo).
* **Distância Total:** Duração e progresso da sessão.

---

## Entregáveis

1.  **Implementação do Jogo (libGDX + IoT)**
    * Código-fonte completo do projeto (obrigatório o uso do **GitHub com commits semanais**).
    * Executável ou APK do jogo.
2.  **Short Paper (Documento de Projeto)**
    * Documento de até 6 páginas, incluindo:
        * Descrição do Problema e Contexto da Reabilitação.
        * Justificativa (Como o jogo potencializa a reabilitação via gamificação das pedaladas).
        * Descrição do Jogo (Funcionalidades, Mecânicas, Integração IoT, com *prints* de tela).

---

## Critérios de Avaliação

O projeto será avaliado com base nos seguintes critérios:

| Critério | Detalhes |
| :--- | :--- |
| **Funcionalidade** | Jogo operacional, uso da coleta de dados das pedaladas (IoT) como **único controle**. |
| **Estrutura** | Existência e funcionamento dos 3 níveis/fases, menu e personagem. |
| **Implementação** | Uso eficiente e correto do framework libGDX. |
| **Criatividade** | Originalidade na solução proposta para o jogo de reabilitação. |
| **Coerência** | Alinhamento entre a proposta de reabilitação e o jogo implementado. |
| **Documentação** | Clareza, Justificativa e organização do *Short Paper*. |
| **Desenvolvimento** | **Commits periódicos** no GitHub e participação durante as aulas. |
