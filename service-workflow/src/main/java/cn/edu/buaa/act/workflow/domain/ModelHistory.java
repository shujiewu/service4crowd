/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.buaa.act.workflow.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="ACT_DE_MODEL_HISTORY")
public class ModelHistory extends AbstractModel {

	@Column(name="model_id")
	protected String modelId;
	
	@Column(name="removal_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date removalDate;
	
	public ModelHistory() {
		super();
	}

}
