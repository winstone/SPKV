namespace java cn.sdp.pkv.thrift

struct IndexInfo {
1: string tableName,
2: list<i32> lb,
3: list<i32> ub,
4: i32 d,
5: list<string> indexColumn,
6: list<string> indexContent
}

struct SPColumn {
1: string name,
2: i32 value
}

struct SPContent {
1: string name,
2: string value
}

struct SPKVObject {
1: string key,
2: list<SPColumn> cols,
3: list<SPContent> content
}

struct SPKVRow {
1: string key,
2: list<i32> cols,
3: list<string> content
4: double distance
}

struct NetInfo {
1: i32 integerIP,
2: i32 port
}

exception PKVException {
1: i32 code,
2: string info
}

service PKVService {
	i32 createIndexTable(1:IndexInfo info),
	i32 insertObject(1:string tbName, 2:SPKVObject obj),
	i32 batchInsertObjects(1:map<string, list<SPKVObject>> objs),
	i32 pointQueryCount(1:string tbName, 2:list<SPColumn> qv),
	list<SPKVObject> pointQuery(1:string tbName, 2:list<SPColumn> qv, 3:list<string> returnColumns),
	i32 rangeQueryCount(1:string tbName, 2:list<SPColumn> ql, 3:list<SPColumn> qu),
	list<SPKVObject> rangeQuery(1:string tbName, 2:list<SPColumn> ql, 3:list<SPColumn> qu, 4:list<string> returnColumns),
	list<SPKVObject> knnQuery(1:string tbName, 2:list<SPColumn> qv, 3:i32 K, 4:list<string> returnColumns)
}