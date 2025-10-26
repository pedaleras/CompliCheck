package br.com.fiap.CompliCheck.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitária para armazenar e compartilhar o estado entre os passos do Cucumber.
 * Utiliza ThreadLocal para garantir que o contexto seja isolado por thread,
 * o que é essencial para paralelizar a execução de cenários.
 */
public class TestContext {
    private static final ThreadLocal<Map<String, Object>> testContexts =
            ThreadLocal.withInitial(HashMap::new);

    /**
     * Armazena um valor no contexto de teste atual.
     * @param key Chave para identificar o valor.
     * @param value O valor a ser armazenado.
     */
    public static void set(String key, Object value) {
        testContexts.get().put(key, value);
    }

    /**
     * Recupera um valor do contexto de teste atual, convertendo-o para o tipo especificado.
     * @param key Chave do valor a ser recuperado.
     * @param type Tipo esperado do valor.
     * @param <T> O tipo de retorno.
     * @return O valor recuperado, ou null se não encontrado.
     */
    public static <T> T get(String key, Class<T> type) {
        Object value = testContexts.get().get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null; // Retorna null se não encontrado ou tipo incompatível
    }

    /**
     * Recupera um valor do contexto de teste atual.
     * @param key Chave do valor a ser recuperado.
     * @return O valor recuperado, ou null se não encontrado.
     */
    public static Object get(String key) {
        return testContexts.get().get(key);
    }

    /**
     * Limpa o contexto de teste atual, removendo todos os valores armazenados.
     * Deve ser chamado antes de cada cenário para garantir isolamento.
     */
    public static void clear() {
        testContexts.remove();
    }
}