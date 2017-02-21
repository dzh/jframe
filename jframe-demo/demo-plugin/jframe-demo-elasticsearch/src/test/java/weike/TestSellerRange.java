/**
 * 
 */
package weike;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Oct 13, 2016 10:31:07 AM
 * @since 1.0
 */
public class TestSellerRange {

    static Logger LOG = LoggerFactory.getLogger(TestSellerRange.class);

    static Pattern P_seller = Pattern.compile("(\\d+)\\s+-?(\\d+)");

    @Test
    public void testActiveSeller() throws Exception {
        doRange("/Users/dzh/Downloads/未过期的卖家+会员列表.txt");
    }

    private void doRange(String file) throws Exception {
        LOG.info("start doRange-{}", file);
        int matchCount = 0;
        int notMatchCount = 0;
        long totalMember = 0;

        // [1-20),[20,50w)-{},[50,100)-{},[100,200)-{},[200,300)-{},[300,400)-{},[400,500)-{},[500,)-{}
        int[] count = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                Matcher m = P_seller.matcher(line);
                if (m.matches()) {
                    matchCount++;
                    // LOG.info("match-{}, {} {}", line, m.group(1),
                    // m.group(2));
                    int member = Integer.parseInt(m.group(1));
                    int mb = member / 1000000;
                    if (mb == 0) {
                        if (member < 20 * 10000) {
                            count[0]++;
                        } else if (member < 50 * 10000) {
                            count[1]++;
                        } else
                            count[2]++;
                    } else if (mb > 0 && mb < 5) {
                        count[mb + 1]++;
                    } else
                        count[6]++;
                    totalMember += member;
                } else {
                    notMatchCount++;
                    LOG.info("not match-{}", line);
                }
            }
        }
        LOG.info(" [1-20)-{},[20,50w)-{},[50,100)-{},[100,200)-{},[200,300)-{},[300,400)-{},[400,500)-{},[500,)-{}", count[0],
                count[1], count[2], count[3], count[4], count[5], count[6],count[7]);
        LOG.info("stop doRange-{} matchSeller-{} notMatchSeller-{} totalMember->{}", file, matchCount, notMatchCount,
                totalMember);
    }

    @Test
    public void testAllSeller() throws Exception {
        doRange("/Users/dzh/Downloads/所有卖家+会员列表.txt");
    }

}
