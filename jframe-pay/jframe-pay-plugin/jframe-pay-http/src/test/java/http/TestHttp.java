/**
 * 
 */
package http;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import jframe.pay.domain.util.HttpUtil;

/**
 * @author dzh
 * @date Nov 26, 2015 11:12:06 AM
 * @since 1.0
 */
public class TestHttp {

    @Test
    public void testParsePara() throws UnsupportedEncodingException {
        String buf = "accessType=0&bizType=000201&certId=3474813271258769001041842579301293446&currencyCode=156&encoding=UTF-8&merId=777290058117915&orderId=20151126110304143826&queryId=201511261103046937778&respCode=00&respMsg=Success!&settleAmt=417300&settleCurrencyCode=156&settleDate=1125&signMethod=01&traceNo=693777&traceTime=1126110304&txnAmt=417300&txnSubType=01&txnTime=20151126110304&txnType=01&version=5.0.0&signature=GUoKf7sg7SkJ7rum5XXwJx4s6fjH0K6CedRurclx0ANVWphmM2BVUrBz2L%2f0AHxkKFv7E%2bhmVEBmuMf3vneFRIIH3MpNCS5hJrywp9CVUNh%2fTNufF9g9HbDFOuxUQJmjjAlthDymGlUOhA2U%2buxm73MZJ1XvL%2bKW1qWYltnwcGtDl%2btx2t5kkXNlSQBpdGLME28Hq0V715cOexJ2MUriyzhGOelrzMCkC3srAjodoLzk5Q3gN%2fLCODSGIFDzmaRBHj0Nq42noZPdfUoFJR%2fLZDbW6ct3PlDGOjtJqlY8DWU4B7ME0aPRS8oOdh6hoEgrgRAgHEGdu6sACHSiKjhcXA%3d%3d";
        System.out.println(HttpUtil.parseHttpParas(buf));
    }

}
