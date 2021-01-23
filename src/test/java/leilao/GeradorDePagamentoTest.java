package leilao;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.GeradorDePagamento;

class GeradorDePagamentoTest {

	private GeradorDePagamento gerador;

	@Mock
	private PagamentoDao pagamentoDao;

	@Mock
	private Clock clock;

	@Captor
	private ArgumentCaptor<Pagamento> captor;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.gerador = new GeradorDePagamento(pagamentoDao, clock);
	}

	@Test
	void deveriaCriarPagamentoParaVencedor() {

		Leilao leilao = leilao();
		Lance vencedor = leilao.getLanceVencedor();

		LocalDate date = LocalDate.of(2021, 01, 23);
		Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();

		Mockito.when(clock.instant()).thenReturn(instant);
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		this.gerador.gerarPagamento(vencedor);

		Mockito.verify(pagamentoDao).salvar(captor.capture());

		Pagamento pagamento = captor.getValue();
		Assert.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
		Assert.assertEquals(vencedor.getValor(), pagamento.getValor());

	}

	private Leilao leilao() {

		Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));
		Lance lance = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

		leilao.propoe(lance);
		leilao.setLanceVencedor(lance);

		return leilao;
	}
}
