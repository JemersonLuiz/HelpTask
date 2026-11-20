package com.example.helpdesk;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpdesk.adapters.ChamadoAdapter;
import com.example.helpdesk.dao.ChamadoDAO;
import com.example.helpdesk.dao.HistoricoChamadoDAO;
import com.example.helpdesk.dao.UsuarioDAO;
import com.example.helpdesk.database.DatabaseHelper;
import com.example.helpdesk.models.Chamado;
import com.example.helpdesk.models.Usuario;
import com.example.helpdesk.utils.SessaoUsuario;
import com.example.helpdesk.utils.Validador;

import java.util.List;

public class MainActivity extends Activity {
    private static final String CODIGO_AUTORIZACAO_TECNICO = "HELPDESK2026";

    private static final String STATUS_ABERTO = "Aberto";
    private static final String STATUS_ANDAMENTO = "Em andamento";
    private static final String STATUS_RESOLVIDO = "Resolvido";
    private static final String STATUS_CANCELADO = "Cancelado";

    private static final String[] TIPOS_USUARIO = {"usuario", "tecnico", "admin"};
    private static final String[] CATEGORIAS_CHAMADO = {
            "Informática", "Elétrica", "Hidráulica", "Estrutura", "Limpeza", "Segurança", "Outro"
    };
    private static final String[] CATEGORIAS_FILTRO = {
            "Todos", "Informática", "Elétrica", "Hidráulica", "Estrutura", "Limpeza", "Segurança", "Outro"
    };
    private static final String[] PRIORIDADES_CHAMADO = {"Baixa", "Média", "Alta"};
    private static final String[] PRIORIDADES_FILTRO = {"Todos", "Baixa", "Média", "Alta"};
    private static final String[] STATUS_FILTRO = {
            "Todos", STATUS_ABERTO, STATUS_ANDAMENTO, STATUS_RESOLVIDO, STATUS_CANCELADO
    };
    private static final String[] STATUS_ATENDIMENTO = {
            STATUS_ABERTO, STATUS_ANDAMENTO, STATUS_RESOLVIDO, STATUS_CANCELADO
    };

    private DatabaseHelper databaseHelper;
    private UsuarioDAO usuarioDAO;
    private ChamadoDAO chamadoDAO;
    private HistoricoChamadoDAO historicoDAO;
    private SessaoUsuario sessaoUsuario;

    private Usuario usuarioLogado;
    private Chamado chamadoSelecionado;
    private ChamadoAdapter chamadoAdapter;
    private boolean listandoComoTecnico;

    private ScrollView rootScroll;

    private LinearLayout sectionWelcome;
    private LinearLayout sectionLogin;
    private LinearLayout sectionCadastro;
    private LinearLayout sectionHomeUsuario;
    private LinearLayout sectionChamadoForm;
    private LinearLayout sectionListaChamados;
    private LinearLayout sectionDetalhes;
    private LinearLayout layoutAtendimentoTecnico;

    private EditText editLoginEmail;
    private EditText editLoginSenha;

    private EditText editCadastroNome;
    private EditText editCadastroEmail;
    private EditText editCadastroSenha;
    private EditText editCadastroConfirmarSenha;
    private EditText editCodigoAutorizacao;
    private Spinner spinnerCadastroTipo;

    private TextView txtHomeUsuarioSaudacao;
    private TextView txtResumoChamados;

    private EditText editChamadoTitulo;
    private EditText editChamadoDescricao;
    private EditText editChamadoLocal;
    private EditText editChamadoAnexo;
    private Spinner spinnerChamadoCategoria;
    private Spinner spinnerChamadoPrioridade;

    private TextView txtListaTitulo;
    private TextView txtListaVazia;
    private Spinner spinnerFiltroStatus;
    private Spinner spinnerFiltroCategoria;
    private Spinner spinnerFiltroPrioridade;
    private EditText editPesquisaChamado;
    private RecyclerView recyclerChamados;
    private Button btnVoltarLista;

    private TextView txtDetalhesTitulo;
    private TextView txtDetalhesConteudo;
    private Spinner spinnerStatusAtendimento;
    private EditText editRespostaTecnica;
    private Button btnCancelarChamado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.seedDadosIniciais();

        usuarioDAO = new UsuarioDAO(this);
        chamadoDAO = new ChamadoDAO(this);
        historicoDAO = new HistoricoChamadoDAO(this);
        sessaoUsuario = new SessaoUsuario(this);

        encontrarViews();
        configurarSpinners();
        configurarRecyclerView();
        configurarCliques();

        usuarioLogado = sessaoUsuario.getUsuario();
        if (usuarioLogado != null) {
            abrirTelaInicialPorPerfil();
        } else {
            mostrarBoasVindas();
        }
    }

    // Liga cada componente do XML a uma variavel Java para que os fluxos sejam controlados por codigo.
    private void encontrarViews() {
        rootScroll = findViewById(R.id.rootScroll);

        sectionWelcome = findViewById(R.id.sectionWelcome);
        sectionLogin = findViewById(R.id.sectionLogin);
        sectionCadastro = findViewById(R.id.sectionCadastro);
        sectionHomeUsuario = findViewById(R.id.sectionHomeUsuario);
        sectionChamadoForm = findViewById(R.id.sectionChamadoForm);
        sectionListaChamados = findViewById(R.id.sectionListaChamados);
        sectionDetalhes = findViewById(R.id.sectionDetalhes);
        layoutAtendimentoTecnico = findViewById(R.id.layoutAtendimentoTecnico);

        editLoginEmail = findViewById(R.id.editLoginEmail);
        editLoginSenha = findViewById(R.id.editLoginSenha);

        editCadastroNome = findViewById(R.id.editCadastroNome);
        editCadastroEmail = findViewById(R.id.editCadastroEmail);
        editCadastroSenha = findViewById(R.id.editCadastroSenha);
        editCadastroConfirmarSenha = findViewById(R.id.editCadastroConfirmarSenha);
        editCodigoAutorizacao = findViewById(R.id.editCodigoAutorizacao);
        spinnerCadastroTipo = findViewById(R.id.spinnerCadastroTipo);

        txtHomeUsuarioSaudacao = findViewById(R.id.txtHomeUsuarioSaudacao);
        txtResumoChamados = findViewById(R.id.txtResumoChamados);

        editChamadoTitulo = findViewById(R.id.editChamadoTitulo);
        editChamadoDescricao = findViewById(R.id.editChamadoDescricao);
        editChamadoLocal = findViewById(R.id.editChamadoLocal);
        editChamadoAnexo = findViewById(R.id.editChamadoAnexo);
        spinnerChamadoCategoria = findViewById(R.id.spinnerChamadoCategoria);
        spinnerChamadoPrioridade = findViewById(R.id.spinnerChamadoPrioridade);

        txtListaTitulo = findViewById(R.id.txtListaTitulo);
        txtListaVazia = findViewById(R.id.txtListaVazia);
        spinnerFiltroStatus = findViewById(R.id.spinnerFiltroStatus);
        spinnerFiltroCategoria = findViewById(R.id.spinnerFiltroCategoria);
        spinnerFiltroPrioridade = findViewById(R.id.spinnerFiltroPrioridade);
        editPesquisaChamado = findViewById(R.id.editPesquisaChamado);
        recyclerChamados = findViewById(R.id.recyclerChamados);
        btnVoltarLista = findViewById(R.id.btnVoltarLista);

        txtDetalhesTitulo = findViewById(R.id.txtDetalhesTitulo);
        txtDetalhesConteudo = findViewById(R.id.txtDetalhesConteudo);
        spinnerStatusAtendimento = findViewById(R.id.spinnerStatusAtendimento);
        editRespostaTecnica = findViewById(R.id.editRespostaTecnica);
        btnCancelarChamado = findViewById(R.id.btnCancelarChamado);
    }

    private void configurarSpinners() {
        configurarSpinner(spinnerCadastroTipo, TIPOS_USUARIO);
        configurarSpinner(spinnerChamadoCategoria, CATEGORIAS_CHAMADO);
        configurarSpinner(spinnerChamadoPrioridade, PRIORIDADES_CHAMADO);
        configurarSpinner(spinnerFiltroStatus, STATUS_FILTRO);
        configurarSpinner(spinnerFiltroCategoria, CATEGORIAS_FILTRO);
        configurarSpinner(spinnerFiltroPrioridade, PRIORIDADES_FILTRO);
        configurarSpinner(spinnerStatusAtendimento, STATUS_ATENDIMENTO);
    }

    private void configurarSpinner(Spinner spinner, String[] valores) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, valores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void configurarRecyclerView() {
        chamadoAdapter = new ChamadoAdapter(chamado -> abrirDetalhesChamado(chamado.getIdChamado()));
        recyclerChamados.setLayoutManager(new LinearLayoutManager(this));
        recyclerChamados.setAdapter(chamadoAdapter);
        recyclerChamados.setNestedScrollingEnabled(false);
    }

    // Centraliza os cliques para deixar o fluxo principal facil de acompanhar.
    private void configurarCliques() {
        findViewById(R.id.btnWelcomeEntrar).setOnClickListener(v -> mostrarLogin());
        findViewById(R.id.btnWelcomeCriarConta).setOnClickListener(v -> mostrarCadastro());

        findViewById(R.id.btnLoginEntrar).setOnClickListener(v -> fazerLogin());
        findViewById(R.id.btnLoginCriarConta).setOnClickListener(v -> mostrarCadastro());
        findViewById(R.id.btnLoginVoltar).setOnClickListener(v -> mostrarBoasVindas());

        findViewById(R.id.btnCadastrarUsuario).setOnClickListener(v -> cadastrarUsuario());
        findViewById(R.id.btnCadastroVoltar).setOnClickListener(v -> mostrarLogin());

        findViewById(R.id.btnAbrirChamado).setOnClickListener(v -> mostrarFormularioChamado());
        findViewById(R.id.btnMeusChamados).setOnClickListener(v -> mostrarListaChamados(false));
        findViewById(R.id.btnLogoutHome).setOnClickListener(v -> sair());

        findViewById(R.id.btnSalvarChamado).setOnClickListener(v -> salvarChamado());
        findViewById(R.id.btnCancelarFormChamado).setOnClickListener(v -> mostrarHomeUsuario());

        findViewById(R.id.btnAplicarFiltros).setOnClickListener(v -> carregarChamados());
        findViewById(R.id.btnVoltarLista).setOnClickListener(v -> abrirTelaInicialPorPerfil());
        findViewById(R.id.btnLogoutLista).setOnClickListener(v -> sair());

        findViewById(R.id.btnSalvarAtendimento).setOnClickListener(v -> salvarAtendimentoTecnico());
        findViewById(R.id.btnCancelarChamado).setOnClickListener(v -> confirmarCancelamentoChamado());
        findViewById(R.id.btnVoltarDetalhes).setOnClickListener(v -> mostrarListaChamados(listandoComoTecnico));
    }

    private void fazerLogin() {
        limparErrosLogin();

        String email = textoCampo(editLoginEmail).toLowerCase();
        String senha = textoCampo(editLoginSenha);

        if (!Validador.emailValido(email)) {
            editLoginEmail.setError("Informe um e-mail válido.");
            return;
        }
        if (Validador.campoVazio(senha)) {
            editLoginSenha.setError("Informe a senha.");
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(email, senha);
        if (usuario == null) {
            mostrarMensagem("E-mail ou senha incorretos.");
            return;
        }

        usuarioLogado = usuario;
        sessaoUsuario.salvar(usuario);
        abrirTelaInicialPorPerfil();
    }

    private void cadastrarUsuario() {
        limparErrosCadastro();

        String nome = textoCampo(editCadastroNome);
        String email = textoCampo(editCadastroEmail).toLowerCase();
        String senha = textoCampo(editCadastroSenha);
        String confirmarSenha = textoCampo(editCadastroConfirmarSenha);
        String tipoUsuario = valorSelecionado(spinnerCadastroTipo);
        String codigoAutorizacao = textoCampo(editCodigoAutorizacao);

        if (Validador.campoVazio(nome)) {
            editCadastroNome.setError("Informe o nome completo.");
            return;
        }
        if (!Validador.emailValido(email)) {
            editCadastroEmail.setError("Informe um e-mail válido.");
            return;
        }
        if (!Validador.senhaValida(senha)) {
            editCadastroSenha.setError("A senha deve ter no mínimo 6 caracteres.");
            return;
        }
        if (!Validador.senhasConferem(senha, confirmarSenha)) {
            editCadastroConfirmarSenha.setError("A confirmação deve ser igual à senha.");
            return;
        }
        if (usuarioDAO.emailExiste(email)) {
            editCadastroEmail.setError("Este e-mail já está cadastrado.");
            return;
        }
        if (!"usuario".equals(tipoUsuario) && !CODIGO_AUTORIZACAO_TECNICO.equals(codigoAutorizacao)) {
            editCodigoAutorizacao.setError("Código obrigatório para técnico ou administrador.");
            return;
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);
        novoUsuario.setTipoUsuario(tipoUsuario);

        long id = usuarioDAO.cadastrarUsuario(novoUsuario);
        if (id > 0) {
            mostrarMensagem("Usuário cadastrado. Faça login para continuar.");
            limparCamposCadastro();
            mostrarLogin();
        } else {
            mostrarMensagem("Não foi possível cadastrar o usuário.");
        }
    }

    private void salvarChamado() {
        String titulo = textoCampo(editChamadoTitulo);
        String descricao = textoCampo(editChamadoDescricao);
        String local = textoCampo(editChamadoLocal);
        String categoria = valorSelecionado(spinnerChamadoCategoria);
        String prioridade = valorSelecionado(spinnerChamadoPrioridade);
        String anexo = textoCampo(editChamadoAnexo);

        if (Validador.campoVazio(titulo)) {
            editChamadoTitulo.setError("Informe o título.");
            return;
        }
        if (Validador.campoVazio(descricao)) {
            editChamadoDescricao.setError("Informe a descrição.");
            return;
        }
        if (Validador.campoVazio(local)) {
            editChamadoLocal.setError("Informe o local.");
            return;
        }

        Chamado chamado = new Chamado();
        chamado.setIdUsuario(usuarioLogado.getIdUsuario());
        chamado.setTitulo(titulo);
        chamado.setDescricao(descricao);
        chamado.setCategoria(categoria);
        chamado.setPrioridade(prioridade);
        chamado.setLocal(local);
        chamado.setAnexoImagem(anexo);

        long idChamado = chamadoDAO.criarChamado(chamado);
        if (idChamado > 0) {
            historicoDAO.registrar((int) idChamado, usuarioLogado.getIdUsuario(),
                    "Chamado criado", "Chamado aberto pelo usuário.", null, STATUS_ABERTO);
            mostrarMensagem("Chamado criado com status Aberto.");
            limparFormularioChamado();
            mostrarHomeUsuario();
        } else {
            mostrarMensagem("Não foi possível salvar o chamado.");
        }
    }

    private void carregarChamados() {
        Integer idUsuarioFiltro = listandoComoTecnico ? null : usuarioLogado.getIdUsuario();
        String status = valorSelecionado(spinnerFiltroStatus);
        String categoria = valorSelecionado(spinnerFiltroCategoria);
        String prioridade = valorSelecionado(spinnerFiltroPrioridade);
        String pesquisa = textoCampo(editPesquisaChamado);

        List<Chamado> chamados = chamadoDAO.listarChamados(idUsuarioFiltro, status, categoria, prioridade, pesquisa);
        chamadoAdapter.atualizar(chamados);
        txtListaVazia.setVisibility(chamados.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void abrirDetalhesChamado(int idChamado) {
        Chamado chamado = chamadoDAO.buscarPorId(idChamado);
        if (chamado == null) {
            mostrarMensagem("Chamado não encontrado.");
            carregarChamados();
            return;
        }

        chamadoSelecionado = chamado;
        txtDetalhesTitulo.setText("Detalhes do chamado #" + chamado.getIdChamado());
        txtDetalhesConteudo.setText(montarTextoDetalhes(chamado));

        boolean tecnicoOuAdmin = usuarioLogado.ehTecnicoOuAdmin();
        layoutAtendimentoTecnico.setVisibility(tecnicoOuAdmin ? View.VISIBLE : View.GONE);
        btnCancelarChamado.setVisibility(devePermitirCancelamentoUsuario(chamado) ? View.VISIBLE : View.GONE);

        if (tecnicoOuAdmin) {
            selecionarValor(spinnerStatusAtendimento, chamado.getStatus());
            editRespostaTecnica.setText(textoOuVazio(chamado.getRespostaTecnico()));
        }

        mostrarTela(sectionDetalhes);
    }

    private void salvarAtendimentoTecnico() {
        if (chamadoSelecionado == null) {
            mostrarMensagem("Selecione um chamado antes de atualizar.");
            return;
        }
        if (!usuarioLogado.ehTecnicoOuAdmin()) {
            mostrarMensagem("Apenas técnico ou administrador pode atualizar chamados.");
            return;
        }

        String statusNovo = valorSelecionado(spinnerStatusAtendimento);
        String resposta = textoCampo(editRespostaTecnica);

        if (Validador.campoVazio(resposta)) {
            editRespostaTecnica.setError("Informe uma resposta ou observação técnica.");
            return;
        }

        String statusAnterior = chamadoSelecionado.getStatus();
        boolean atualizado = chamadoDAO.atualizarAtendimento(
                chamadoSelecionado.getIdChamado(),
                usuarioLogado.getIdUsuario(),
                statusNovo,
                resposta
        );

        if (atualizado) {
            historicoDAO.registrar(chamadoSelecionado.getIdChamado(), usuarioLogado.getIdUsuario(),
                    "Atendimento atualizado", resposta, statusAnterior, statusNovo);
            mostrarMensagem("Chamado atualizado.");
            abrirDetalhesChamado(chamadoSelecionado.getIdChamado());
        } else {
            mostrarMensagem("Não foi possível atualizar o chamado.");
        }
    }

    private void confirmarCancelamentoChamado() {
        if (chamadoSelecionado == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Cancelar chamado")
                .setMessage("Deseja cancelar este chamado? Esta ação só é permitida enquanto ele está Aberto.")
                .setPositiveButton("Cancelar chamado", (dialog, which) -> cancelarChamadoUsuario())
                .setNegativeButton("Voltar", null)
                .show();
    }

    private void cancelarChamadoUsuario() {
        String statusAnterior = chamadoSelecionado.getStatus();
        boolean cancelado = chamadoDAO.cancelarChamadoUsuario(
                chamadoSelecionado.getIdChamado(),
                usuarioLogado.getIdUsuario()
        );

        if (cancelado) {
            historicoDAO.registrar(chamadoSelecionado.getIdChamado(), usuarioLogado.getIdUsuario(),
                    "Chamado cancelado", "Cancelamento feito pelo usuário.", statusAnterior, STATUS_CANCELADO);
            mostrarMensagem("Chamado cancelado.");
            mostrarListaChamados(false);
        } else {
            mostrarMensagem("Somente chamados abertos podem ser cancelados pelo usuário.");
            abrirDetalhesChamado(chamadoSelecionado.getIdChamado());
        }
    }

    private void abrirTelaInicialPorPerfil() {
        if (usuarioLogado == null) {
            mostrarBoasVindas();
            return;
        }
        if (usuarioLogado.ehTecnicoOuAdmin()) {
            mostrarListaChamados(true);
        } else {
            mostrarHomeUsuario();
        }
    }

    private void mostrarBoasVindas() {
        mostrarTela(sectionWelcome);
    }

    private void mostrarLogin() {
        mostrarTela(sectionLogin);
    }

    private void mostrarCadastro() {
        mostrarTela(sectionCadastro);
    }

    private void mostrarHomeUsuario() {
        int abertos = chamadoDAO.contarPorStatus(usuarioLogado.getIdUsuario(), STATUS_ABERTO);
        int emAndamento = chamadoDAO.contarPorStatus(usuarioLogado.getIdUsuario(), STATUS_ANDAMENTO);
        int resolvidos = chamadoDAO.contarPorStatus(usuarioLogado.getIdUsuario(), STATUS_RESOLVIDO);

        txtHomeUsuarioSaudacao.setText("Olá, " + usuarioLogado.getNome());
        txtResumoChamados.setText(
                "Resumo dos seus chamados\n\n" +
                        "Abertos: " + abertos + "\n" +
                        "Em andamento: " + emAndamento + "\n" +
                        "Resolvidos: " + resolvidos
        );

        mostrarTela(sectionHomeUsuario);
    }

    private void mostrarFormularioChamado() {
        limparFormularioChamado();
        mostrarTela(sectionChamadoForm);
    }

    private void mostrarListaChamados(boolean tecnico) {
        listandoComoTecnico = tecnico;

        txtListaTitulo.setText(tecnico ? "Chamados para atendimento" : "Meus chamados");
        btnVoltarLista.setVisibility(tecnico ? View.GONE : View.VISIBLE);

        carregarChamados();
        mostrarTela(sectionListaChamados);
    }

    private void mostrarTela(View telaAtiva) {
        sectionWelcome.setVisibility(View.GONE);
        sectionLogin.setVisibility(View.GONE);
        sectionCadastro.setVisibility(View.GONE);
        sectionHomeUsuario.setVisibility(View.GONE);
        sectionChamadoForm.setVisibility(View.GONE);
        sectionListaChamados.setVisibility(View.GONE);
        sectionDetalhes.setVisibility(View.GONE);

        telaAtiva.setVisibility(View.VISIBLE);
        rootScroll.post(() -> rootScroll.smoothScrollTo(0, 0));
    }

    private String montarTextoDetalhes(Chamado chamado) {
        return "ID: " + chamado.getIdChamado() + "\n" +
                "Solicitante: " + textoOuVazio(chamado.getNomeSolicitante()) + "\n" +
                "Título: " + chamado.getTitulo() + "\n" +
                "Descrição: " + chamado.getDescricao() + "\n" +
                "Categoria: " + chamado.getCategoria() + "\n" +
                "Prioridade: " + chamado.getPrioridade() + "\n" +
                "Local: " + chamado.getLocal() + "\n" +
                "Status: " + chamado.getStatus() + "\n" +
                "Data de abertura: " + textoOuVazio(chamado.getDataAbertura()) + "\n" +
                "Última atualização: " + textoOuVazio(chamado.getDataAtualizacao()) + "\n" +
                "Resposta técnica: " + textoOuVazio(chamado.getRespostaTecnico()) + "\n" +
                "Anexo: " + textoOuVazio(chamado.getAnexoImagem());
    }

    private boolean devePermitirCancelamentoUsuario(Chamado chamado) {
        return !usuarioLogado.ehTecnicoOuAdmin()
                && usuarioLogado.getIdUsuario() == chamado.getIdUsuario()
                && STATUS_ABERTO.equals(chamado.getStatus());
    }

    private String valorSelecionado(Spinner spinner) {
        Object item = spinner.getSelectedItem();
        return item == null ? "" : item.toString();
    }

    private void selecionarValor(Spinner spinner, String valor) {
        if (valor == null) {
            return;
        }
        for (int i = 0; i < spinner.getCount(); i++) {
            if (valor.equalsIgnoreCase(spinner.getItemAtPosition(i).toString())) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private String textoCampo(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String textoOuVazio(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    private void limparErrosLogin() {
        editLoginEmail.setError(null);
        editLoginSenha.setError(null);
    }

    private void limparErrosCadastro() {
        editCadastroNome.setError(null);
        editCadastroEmail.setError(null);
        editCadastroSenha.setError(null);
        editCadastroConfirmarSenha.setError(null);
        editCodigoAutorizacao.setError(null);
    }

    private void limparCamposCadastro() {
        editCadastroNome.setText("");
        editCadastroEmail.setText("");
        editCadastroSenha.setText("");
        editCadastroConfirmarSenha.setText("");
        editCodigoAutorizacao.setText("");
        spinnerCadastroTipo.setSelection(0);
    }

    private void limparFormularioChamado() {
        editChamadoTitulo.setText("");
        editChamadoDescricao.setText("");
        editChamadoLocal.setText("");
        editChamadoAnexo.setText("");
        spinnerChamadoCategoria.setSelection(0);
        spinnerChamadoPrioridade.setSelection(0);
    }

    private void sair() {
        sessaoUsuario.limpar();
        usuarioLogado = null;
        chamadoSelecionado = null;
        editLoginSenha.setText("");
        mostrarBoasVindas();
    }

    private void mostrarMensagem(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }
}
