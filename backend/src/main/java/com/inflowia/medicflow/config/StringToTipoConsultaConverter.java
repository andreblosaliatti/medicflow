import org.springframework.core.convert.converter.Converter;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;

public class StringToTipoConsultaConverter implements Converter<String, TipoConsulta> {

    @Override
    public TipoConsulta convert(String source) {
        return TipoConsulta.valueOf(source.toUpperCase());
    }
}