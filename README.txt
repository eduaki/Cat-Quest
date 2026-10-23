================================================================
 Cat Quest  - APLICATIVO ANDROID
 Trabalho da disciplina de Desenvolvimento Mobile - 6º semestre
================================================================

INTEGRANTES DA EQUIPE
----------------------------------------------------------------
 Eduardo Alves Candido - RGM: 37735349
 Weverton Vieira Ribeiro - RGM: 39515923

1. OBJETIVO DO APLICATIVO
----------------------------------------------------------------
 O Cat Quest é um app de cuidado diário com gatos que transforma
 tarefas simples em um jogo afetivo.

 O tutor recebe missões do dia (trocar a água, servir o sachê, limpar
 a caixa de areia, brincar, escovar e dar carinho) e comprova cada uma
 enviando uma foto. Ao concluir, ganha pontos, "peixinhos" (moeda do
 jogo) e mantém uma sequência de dias.

 Objetivo principal: ajudar o tutor a lembrar e registrar os cuidados
 diários com o gato de forma leve, visual e divertida - sem cobrança e
 sem necessidade de criar conta, pois todos os dados ficam no próprio
 dispositivo (funciona offline).


2. FUNCIONALIDADES
----------------------------------------------------------------
 * Missões diárias
   - Seis missões padrão criadas automaticamente a cada dia.
   - Criação de missões personalizadas (título, descrição e categoria).
   - Conclusão comprovada por foto: câmera interna do app, câmera do
     sistema, galeria ou selo ilustrado gerado pelo próprio app.
   - Possibilidade de desfazer a conclusão ou excluir a missão.
   - Comentário do gato sorteado a cada missão concluída.

 * Calendário
   - Grade mensal com os dias em que houve missões concluídas.
   - Seleção de qualquer data para ver ou gerenciar as missões.
   - Gráfico de barras da semana e resumo da sequência (streak).

 * Retrospectiva musical do mês
   - Carrossel com as fotos do mês e estatísticas do período.
   - Trilha sonora chiptune gerada em tempo real (5 melodias) com
     controle de play/mudo, ou áudio escolhido pelo usuário.
   - Compartilhamento do resumo com amigos.

 * Mural de fotos
   - Galeria com todas as comprovações fotográficas.
   - Filtro por mês ou todas as fotos, visualização ampliada com os
     detalhes da missão e compartilhamento.

 * Lojinha de recompensas
   - Abas de Acessórios, Pelagens e Temas.
   - Compra com peixinhos e equipagem imediata no gatinho cartoon.
   - Prévia ao vivo do gato personalizado.

 * Lembretes e mensagens de carinho
   - Três horários diários configuráveis (manhã, tarde e noite).
   - Três tons de voz do gato: Carinhoso, Brincalhão e Exigente.
   - Notificações com som/vibração e pedido de permissão.
   - Banco de mensagens afetivas que pode ser criado, editado e
     excluído pelo usuário.
   - Perfil editável do gato: nome, gênero, raça, petisco favorito e
     pelagem.

 * Gamificação
   - Pontos por missão, bônus de sequência, nível do tutor e peixinhos.
   - Itens gratuitos e pagos (pelagens, acessórios e temas).


3. PÚBLICO-ALVO
----------------------------------------------------------------
 Tutores de gatos que querem criar uma rotina de cuidados e guardar um
 álbum afetivo do dia a dia com o animal.


4. TECNOLOGIAS UTILIZADAS
----------------------------------------------------------------
 - Linguagem: Kotlin
 - Interface: Jetpack Compose (Material 3)
 - Banco de dados local: Room
 - Câmera: CameraX
 - Notificações: AlarmManager + NotificationManager
 - Áudio: síntese de som (chiptune) e MediaPlayer
 - Arquitetura: MVVM + Repository, com dados local-first
 - Android: minSdk 24 / targetSdk 36


5. COMO EXECUTAR O PROJETO
----------------------------------------------------------------
 1. Abra a pasta do projeto no Android Studio.
 2. Aguarde a sincronização do Gradle.
 3. Conecte um dispositivo ou inicie um emulador com Android 7.0+.
 4. Execute o módulo "app" (Run 'app').
 5. Na primeira abertura, permita o acesso à câmera e às notificações
    para aproveitar todos os recursos.


6. ORGANIZAÇÃO DO CÓDIGO (RESUMO)
----------------------------------------------------------------
 app/src/main/java/com/example
   data/           modelos, banco Room (DAOs) e repositório
   ui/             telas em Compose, componentes, tema e ViewModel
   notifications/  agendamento e envio das notificações
   audio/          sintetizador da trilha sonora
   utils/          utilitários (datas, fotos e pontuação)
   MainActivity.kt / CatCareApplication.kt  ponto de entrada


7. OBSERVAÇÕES
----------------------------------------------------------------
 - O app funciona totalmente offline: não precisa de login nem de
   servidor.
 - As fotos ficam salvas no armazenamento interno do aplicativo,
  garantindo a privacidade do usuário.
================================================================