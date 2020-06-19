import React, { ReactElement, useState } from 'react';
import { AutoComplete, Input, Button } from 'antd';
import { SearchOutlined } from '@ant-design/icons/lib';
import { Completion, completionsFor } from './completions';
import completionsCreatedBy from './completionsCreatedBy.json';
import completionsTopics from './completionsTopics.json';
import s from './styles.module.less';

interface Props {
  onSearch: (query: string) => void;
}

const getCompletions = completionsFor({
  topics: completionsTopics,
  channels: completionsCreatedBy,
});

const SearchBar = ({ onSearch }: Props): ReactElement => {
  const [result, setResult] = useState<Completion[]>([]);
  const [inputValue, setInputValue] = useState<string>();
  const onChange = (txt: string) => {
    setResult(getCompletions(txt));
  };

  const renderResult = (r: Completion) => (
    <div className={s.result}>
      {r.list === 'channels' && (
        <span className={s.channelAffix}>Channel:&nbsp;</span>
      )}

      {r.textWithHighlights.map((chunk) => (
        <span
          className={chunk.matches ? '' : s.completionAffix}
          key={chunk.text}
        >
          {chunk.text}
        </span>
      ))}
    </div>
  );

  const optionsRender = () =>
    result.map((r: Completion) => ({
      label: renderResult(r),
      value: r.text,
    }));

  const getInputValue = (e: any) => {
    setInputValue(e.currentTarget.value);
  };

  const handleSearchButton = () => {
    onSearch(inputValue!!);
  };

  return (
    <>
      <AutoComplete
        defaultActiveFirstOption={false}
        backfill
        dropdownMatchSelectWidth={false}
        options={optionsRender()}
        onSearch={onChange}
        className={s.autoCompleteWrapper}
        dropdownClassName={s.dropdownWrapper}
        onSelect={onSearch}
      >
        <Input
          size="middle"
          onChange={getInputValue}
          placeholder="Search for videos"
          data-qa="search-input"
          aria-label="search"
          prefix={<SearchOutlined />}
        />
      </AutoComplete>
      <Button
        onClick={handleSearchButton}
        type="primary"
        className={s.searchButton}
      >
        Search
      </Button>
    </>
  );
};

export default SearchBar;
