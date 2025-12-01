package br.com.ifpe.olx_pp1_api.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

public class Util {

    // ATENÇÃO: Altere este caminho para onde você quer armazenar as imagens
    public static final String LOCAL_ARMAZENAMENTO_IMAGENS = "C:/Users/debor/Documents/estudos/pp1/olx-pp1-api/public";

    public static String fazerUploadImagem(MultipartFile imagem) {

        boolean sucessoUpload = false;
        String nomeArquivoComDataHora = null;

        if (imagem != null && !imagem.isEmpty()) {

            String dataHora = LocalDateTime.now().getYear() + "-"
               + LocalDateTime.now().getMonthValue() + "-"
               + LocalDateTime.now().getDayOfMonth() + "-"
               + LocalDateTime.now().getHour() + "-"
               + LocalDateTime.now().getMinute() + "-"
               + LocalDateTime.now().getSecond() + " - ";

           String nomeOriginalArquivo = imagem.getOriginalFilename();
           nomeArquivoComDataHora = dataHora + nomeOriginalArquivo;

            try {

                // Criando o diretório para armazenar o arquivo
                String imagens_projetos = LOCAL_ARMAZENAMENTO_IMAGENS + "/imagens_produtos";
                File dir = new File(imagens_projetos);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // Criando o arquivo no diretório
                File serverFile = new File(dir.getAbsolutePath() + File.separator + nomeArquivoComDataHora);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(imagem.getBytes());
                stream.close();

                System.out.println("Arquivo armazenado em:" + serverFile.getAbsolutePath());
                System.out.println("Você fez o upload do arquivo " + nomeOriginalArquivo + " com sucesso");
                sucessoUpload = true;

            } catch (Exception e) {
                System.out.println("Você falhou em carregar o arquivo " + nomeOriginalArquivo + " => " + e.getMessage());
            }

        } else {
            System.out.println("Você falhou em carregar o arquivo porque ele está vazio ");
        }

        if (sucessoUpload) {
            return nomeArquivoComDataHora;
        } else {
            return null;
        }
    }
}