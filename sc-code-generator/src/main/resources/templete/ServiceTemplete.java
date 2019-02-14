#set($domain=$!domainName.substring(0,1).toLowerCase()+$!domainName.substring(1))
package $!{packageName};

import com.fg.commons.hessian.core.Hessian;
import com.fg.commons.hessian.header.HeaderInfo;
import sc.commons.base.api.IBaseService;
import sc.commons.base.validator.DataValidator;

/**
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Hessian(context = "$!{hessionUrl}", uri = "/$!{domain}Service")
public interface I$!{domainName}Service extends IBaseService<$!{domainName}Request,$!{domainName}Response>{
@Override
     $!{domainName}Response call(HeaderInfo headerInfo,$!{domainName}Request $!{domain}Request);
             }
